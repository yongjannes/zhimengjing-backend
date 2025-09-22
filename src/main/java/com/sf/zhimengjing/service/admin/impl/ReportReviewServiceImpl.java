package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.enumerate.ReviewResult;
import com.sf.zhimengjing.common.enumerate.ReviewStatus;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ReportReviewDTO;
import com.sf.zhimengjing.entity.admin.*;
import com.sf.zhimengjing.mapper.admin.ContentReportMapper;
import com.sf.zhimengjing.mapper.admin.ReportReviewMapper;
import com.sf.zhimengjing.mapper.admin.SensitiveWordMapper;
import com.sf.zhimengjing.service.admin.CommunityContentService;
import com.sf.zhimengjing.service.admin.ReportReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: ReportReviewServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: 报告审核服务实现类，提供报告审核相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class ReportReviewServiceImpl extends ServiceImpl<ReportReviewMapper, ReportReview> implements ReportReviewService {

    private final CommunityContentService communityContentService;
    private final ContentReportMapper contentReportMapper;
    private final SensitiveWordMapper sensitiveWordMapper;

    /** 获取待审核报告列表（分页） */
    @Override
    public IPage<ReportReviewDTO> getPendingReviews(Page<ReportReviewDTO> page, Long reviewerId) {
        // 构造查询条件：状态为待审核或审核中，且可按审核员过滤
        LambdaQueryWrapper<ReportReview> wrapper = new LambdaQueryWrapper<ReportReview>()
                .in(ReportReview::getReviewStatus, ReviewStatus.PENDING.name(), ReviewStatus.UNDER_REVIEW.name())
                .eq(reviewerId != null, ReportReview::getReviewerId, reviewerId)
                .orderByAsc(ReportReview::getCreateTime);

        // 执行分页查询
        IPage<ReportReview> entityPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);

        // 转换为 DTO 并返回
        return entityPage.convert(this::convertToDTO);
    }

    /** 获取单条审核记录详情 */
    @Override
    public ReportReviewDTO getReviewDetail(Long reviewId) {
        return convertToDTO(this.getById(reviewId));
    }

    /** 提交审核结果 */
    @Override
    @Transactional
    public boolean submitReview(ReportReviewDTO.ReviewRequestDTO dto, Long reviewerId) {
        // 查询审核记录
        ReportReview review = this.getOne(new LambdaQueryWrapper<ReportReview>().eq(ReportReview::getReportId, dto.getReportId()));

        // 检查记录是否存在及状态是否允许审核
        if (review == null || review.getReviewStatus().equals(ReviewStatus.REVIEWED.name())) {
            throw new RuntimeException("报告不存在或已审核");
        }

        // 设置审核信息
        review.setReviewerId(reviewerId);
        review.setReviewStatus(ReviewStatus.REVIEWED.name());
        review.setReviewResult(dto.getReviewResult());
        review.setReviewScore(dto.getReviewScore());
        review.setReviewComment(dto.getReviewComment());
        review.setViolationType(dto.getViolationType());
        review.setViolationDetails(dto.getViolationDetails());
        review.setReviewedAt(LocalDateTime.now());

        // 更新数据库
        return this.updateById(review);
    }

    /** 批量分配审核任务 */
    @Override
    @Transactional
    public boolean assignReviewTasks(ReportReviewDTO.BatchAssignDTO dto, Long operatorId) {
        // 将每个报告生成审核记录
        List<ReportReview> reviews = dto.getReportIds().stream().map(reportId -> {
            ReportReview review = new ReportReview();
            review.setReportId(reportId);
            review.setReviewerId(dto.getReviewerId());
            review.setReviewStatus(ReviewStatus.UNDER_REVIEW.name());
            return review;
        }).collect(Collectors.toList());

        // 批量保存审核记录
        return this.saveBatch(reviews);
    }

    /** 用户提交申诉 */
    @Override
    @Transactional
    public boolean submitAppeal(Long reviewId, String appealReason, Long userId) {
        // 查询审核记录
        ReportReview review = this.getById(reviewId);

        // 检查状态是否允许申诉
        if (review == null || !review.getReviewStatus().equals(ReviewStatus.REVIEWED.name())) {
            throw new RuntimeException("审核记录不存在或状态不正确");
        }

        // 更新申诉状态
        review.setIsAppealed(true);
        review.setReviewStatus(ReviewStatus.APPEALED.name());

        return this.updateById(review);
    }

    /** 管理员处理申诉 */
    @Override
    @Transactional
    public boolean handleAppeal(Long reviewId, String appealResult, String comment, Long handlerId) {
        // 查询审核记录
        ReportReview review = this.getById(reviewId);

        // 检查申诉状态
        if (review == null || !review.getReviewStatus().equals(ReviewStatus.APPEALED.name())) {
            throw new RuntimeException("申诉记录不存在或状态不正确");
        }

        // 更新申诉处理结果
        review.setAppealResult(appealResult);
        review.setReviewComment(comment);
        review.setReviewStatus(ReviewStatus.APPEAL_RESOLVED.name());
        review.setReviewerId(handlerId);

        return this.updateById(review);
    }

    /** 获取审核统计信息 */
    @Override
    public ReportReviewDTO.ReviewStatsVO getReviewStats(Long reviewerId, LocalDate startDate, LocalDate endDate) {
        // 构造基础查询条件
        LambdaQueryWrapper<ReportReview> baseWrapper = new LambdaQueryWrapper<ReportReview>()
                .eq(reviewerId != null, ReportReview::getReviewerId, reviewerId)
                .ge(startDate != null, ReportReview::getCreateTime, startDate.atStartOfDay())
                .le(endDate != null, ReportReview::getCreateTime, endDate.plusDays(1).atStartOfDay());

        ReportReviewDTO.ReviewStatsVO vo = new ReportReviewDTO.ReviewStatsVO();

        // 统计不同状态和结果
        long totalReviews = this.count(baseWrapper.clone().in(ReportReview::getReviewStatus, ReviewStatus.REVIEWED.name(), ReviewStatus.APPEALED.name(), ReviewStatus.APPEAL_RESOLVED.name()));
        long approvedCount = this.count(baseWrapper.clone().eq(ReportReview::getReviewResult, ReviewResult.APPROVED.name()));

        vo.setTotalReviews(totalReviews);
        vo.setApprovedCount(approvedCount);
        vo.setRejectedCount(this.count(baseWrapper.clone().eq(ReportReview::getReviewResult, ReviewResult.REJECTED.name())));
        vo.setViolationCount(this.count(baseWrapper.clone().eq(ReportReview::getReviewResult, ReviewResult.VIOLATION.name())));
        vo.setPendingCount(this.count(new LambdaQueryWrapper<ReportReview>().in(ReportReview::getReviewStatus, ReviewStatus.PENDING.name(), ReviewStatus.UNDER_REVIEW.name())));
        vo.setAppealCount(this.count(new LambdaQueryWrapper<ReportReview>().eq(ReportReview::getIsAppealed, true)));
        vo.setApprovalRate(totalReviews > 0 ? (double) approvedCount / totalReviews * 100 : 0.0);

        return vo;
    }

    /**
     * 自动审核检查（最终正确版）
     * @return 返回审核后的 ReportReview 对象
     */
    @Override
    @Transactional
    public ReportReview autoReviewCheck(Long reportId) {
        // 1. 查询审核记录
        ReportReview review = this.getOne(
                new LambdaQueryWrapper<ReportReview>().eq(ReportReview::getReportId, reportId)
        );
        if (review == null) {
            throw new GeneralBusinessException("报告审核记录不存在");
        }

        final List<String> processedStatus = Arrays.asList(
                ReviewStatus.REVIEWED.name(),
                ReviewStatus.UNDER_REVIEW.name(),
                ReviewStatus.APPEALED.name(),
                ReviewStatus.APPEAL_RESOLVED.name()
        );

        String currentStatus = review.getReviewStatus();

        if (StringUtils.hasText(currentStatus) && processedStatus.contains(currentStatus)) {
            throw new GeneralBusinessException("报告ID：" + reportId + " 已被处理，当前状态为：" + currentStatus);
        }

        // 2. 获取被举报的原始内容
        ContentReport contentReport = contentReportMapper.selectById(review.getReportId());
        if (contentReport == null) {
            throw new GeneralBusinessException("关联的举报内容不存在");
        }
        String contentText = getContentText(contentReport.getContentType(), contentReport.getContentId());

        // 如果内容为空或获取失败，直接通过
        if (!StringUtils.hasText(contentText)) {
            review.setReviewStatus(ReviewStatus.REVIEWED.name());
            review.setReviewResult(ReviewResult.APPROVED.name());
            review.setReviewComment("自动审核：内容为空或无法获取，直接通过");
            this.updateById(review);
            return review;
        }

        // 3. 获取所有敏感词
        List<SensitiveWord> sensitiveWords = sensitiveWordMapper.selectList(null);
        if (CollectionUtils.isEmpty(sensitiveWords)) {
            review.setReviewStatus(ReviewStatus.REVIEWED.name());
            review.setReviewResult(ReviewResult.APPROVED.name());
            review.setReviewComment("自动审核：未配置敏感词，默认通过");
            this.updateById(review);
            return review;
        }

        // 4. 内容与敏感词匹配
        List<String> hitWords = sensitiveWords.stream()
                .map(SensitiveWord::getWord)
                .filter(contentText::contains)
                .collect(Collectors.toList());

        // 5. 根据匹配结果更新审核状态
        if (hitWords.isEmpty()) {
            review.setReviewStatus(ReviewStatus.REVIEWED.name());
            review.setReviewResult(ReviewResult.APPROVED.name());
            review.setReviewComment("自动审核：内容合规");
        } else {
            review.setReviewStatus(ReviewStatus.REVIEWED.name());
            review.setReviewResult(ReviewResult.VIOLATION.name());
            review.setViolationType("TEXT_VIOLATION");
            String violationDetails = "内容包含敏感词：" + String.join(", ", hitWords);
            review.setViolationDetails(violationDetails);
            review.setReviewComment("自动审核：发现违规内容");
        }

        review.setReviewedAt(LocalDateTime.now());
        review.setReviewerId(0L); // 0L 代表系统自动审核

        boolean updated = this.updateById(review);
        if (!updated) {
            throw new GeneralBusinessException("自动审核失败，数据库更新异常");
        }

        return review;
    }

    /**
     * 根据举报类型和ID，通过 CommunityContentService 获取文本内容
     */
    private String getContentText(Integer contentType, Long contentId) {
        if (contentType == null || contentId == null) {
            return null;
        }
        // 根据 ContentReport 表的定义 (1=帖子, 2=评论)
        if (contentType == 1) {
            CommunityPost post = communityContentService.getPostDetail(contentId);
            return post != null ? post.getContent() : null;
        } else if (contentType == 2) {
            CommunityComment comment = communityContentService.getCommentDetail(contentId);
            return comment != null ? comment.getContent() : null;
        }
        return null;
    }


    /** 获取审核员的审核记录列表（分页） */
    @Override
    public IPage<ReportReviewDTO> getMyReviews(Page<ReportReviewDTO> page, Long reviewerId) {
        // 查询该审核员的审核记录，按创建时间倒序
        LambdaQueryWrapper<ReportReview> wrapper = new LambdaQueryWrapper<ReportReview>()
                .eq(ReportReview::getReviewerId, reviewerId)
                .orderByDesc(ReportReview::getCreateTime);

        IPage<ReportReview> entityPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);

        // 转换为 DTO 并返回
        return entityPage.convert(this::convertToDTO);
    }

    /** 根据报告ID获取对应审核记录 */
    @Override
    public ReportReviewDTO getReviewByReportId(Long reportId) {
        ReportReview review = this.getOne(new LambdaQueryWrapper<ReportReview>().eq(ReportReview::getReportId, reportId));
        return convertToDTO(review);
    }

    /** 实体转换 DTO */
    private ReportReviewDTO convertToDTO(ReportReview entity) {
        if (entity == null) return null;

        ReportReviewDTO dto = new ReportReviewDTO();
        dto.setId(entity.getId());
        dto.setReportId(entity.getReportId());
        dto.setReviewerId(entity.getReviewerId());
        dto.setReviewerName(entity.getReviewerId() != null ? "审核员" + entity.getReviewerId() : null);
        dto.setReviewResult(entity.getReviewResult());
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setCreateTime(entity.getCreateTime());

        // 核心：String -> 枚举
        if (entity.getReviewStatus() != null) {
            try {
                dto.setReviewStatus(ReviewStatus.valueOf(entity.getReviewStatus()));
            } catch (IllegalArgumentException e) {
                dto.setReviewStatus(null);
            }
        }

        return dto;
    }

}
