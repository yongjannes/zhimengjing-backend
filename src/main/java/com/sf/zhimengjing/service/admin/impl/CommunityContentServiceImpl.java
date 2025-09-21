package com.sf.zhimengjing.service.admin.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.*;
import com.sf.zhimengjing.common.model.vo.ContentStatisticsVO;
import com.sf.zhimengjing.common.model.vo.ReportStatisticsVO;
import com.sf.zhimengjing.entity.admin.CommunityComment;
import com.sf.zhimengjing.entity.admin.CommunityPost;
import com.sf.zhimengjing.entity.admin.ContentReport;
import com.sf.zhimengjing.mapper.admin.CommunityCommentMapper;
import com.sf.zhimengjing.mapper.admin.CommunityPostMapper;
import com.sf.zhimengjing.mapper.admin.ContentReportMapper;
import com.sf.zhimengjing.service.admin.CommunityContentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @Title: CommunityContentServiceImpl
 * @Author: 殇枫
 * @Description: 社区内容服务实现类，包括帖子、评论、举报的增删改查、审核、统计和导出功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityContentServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost>
        implements CommunityContentService {

    private final CommunityPostMapper postMapper;
    private final CommunityCommentMapper commentMapper;
    private final ContentReportMapper reportMapper;


    // ========================== 帖子相关 ==========================

    /**
     * 分页查询帖子列表
     */
    @Override
    public IPage<CommunityPost> getPostPage(PostQueryDTO queryDTO) {
        Page<CommunityPost> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return postMapper.selectPostPage(page, queryDTO);
    }

    /**
     * 根据ID获取帖子详情
     */
    @Override
    public CommunityPost getPostDetail(Long postId) {
        return postMapper.selectPostDetailById(postId);
    }

    /**
     * 审核单条帖子
     */
    @Override
    @Transactional
    public boolean auditPost(PostAuditDTO auditDTO) {
        // 调用批量审核方法
        return batchAuditPosts(Collections.singletonList(auditDTO.getPostIds().get(0)),
                auditDTO.getStatus(),
                auditDTO.getRejectReason(),
                0L);
    }

    /**
     * 批量审核帖子
     */
    @Override
    @Transactional
    public boolean batchAuditPosts(List<Long> postIds, Integer status, String rejectReason, Long operatorId) {
        if (status == 2 && !StringUtils.hasText(rejectReason)) {
            throw new GeneralBusinessException("拒绝时必须填写原因");
        }

        List<CommunityPost> postsToUpdate = new ArrayList<>();
        for (Long postId : postIds) {
            CommunityPost post = new CommunityPost();
            post.setId(postId);
            post.setStatus(status);
            post.setAdminRemark("操作人ID: " + operatorId);

            // 设置审核结果
            if (status == 1) { // 通过
                post.setPublishedAt(LocalDateTime.now());
                post.setRejectReason(null);
            } else { // 拒绝
                post.setRejectReason(rejectReason);
            }

            postsToUpdate.add(post);
        }

        // 批量更新数据库
        return this.updateBatchById(postsToUpdate);
    }

    /**
     * 更新帖子信息
     */
    @Override
    @Transactional
    public boolean updatePost(PostUpdateDTO updateDTO) {
        CommunityPost post = new CommunityPost();
        BeanUtils.copyProperties(updateDTO, post);
        return this.updateById(post);
    }

    /**
     * 删除单条帖子（逻辑删除）
     */
    @Override
    @Transactional
    public boolean deletePost(Long postId, Long operatorId) {
        return batchDeletePosts(Collections.singletonList(postId), operatorId);
    }

    /**
     * 批量删除帖子（逻辑删除，同时删除评论）
     */
    @Override
    @Transactional
    public boolean batchDeletePosts(List<Long> postIds, Long operatorId) {
        List<CommunityPost> postsToUpdate = new ArrayList<>();

        for (Long postId : postIds) {
            CommunityPost post = new CommunityPost();
            post.setId(postId);
            post.setStatus(3); // 3 = 已删除
            post.setAdminRemark("删除操作人ID: " + operatorId);
            postsToUpdate.add(post);
        }

        // 同时逻辑删除评论
        LambdaQueryWrapper<CommunityComment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.in(CommunityComment::getPostId, postIds);

        CommunityComment commentUpdate = new CommunityComment();
        commentUpdate.setStatus(3); // 3 = 已删除
        commentMapper.update(commentUpdate, commentWrapper);

        return this.updateBatchById(postsToUpdate);
    }

    /**
     * 置顶/取消置顶帖子
     */
    @Override
    @Transactional
    public boolean togglePostTop(Long postId, Boolean isTop, Long operatorId) {
        CommunityPost post = new CommunityPost();
        post.setId(postId);
        post.setIsTop(isTop);
        post.setAdminRemark("置顶操作人ID: " + operatorId);
        return this.updateById(post);
    }

    /**
     * 热门/取消热门帖子
     */
    @Override
    @Transactional
    public boolean togglePostHot(Long postId, Boolean isHot, Long operatorId) {
        CommunityPost post = new CommunityPost();
        post.setId(postId);
        post.setIsHot(isHot);
        post.setAdminRemark("热门操作人ID: " + operatorId);
        return this.updateById(post);
    }

    // ========================== 评论相关 ==========================

    @Override
    public IPage<CommunityComment> getCommentPage(CommentQueryDTO queryDTO) {
        Page<CommunityComment> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return commentMapper.selectCommentPage(page, queryDTO);
    }

    @Override
    public CommunityComment getCommentDetail(Long commentId) {
        return commentMapper.selectById(commentId);
    }

    @Override
    @Transactional
    public boolean auditComment(CommentAuditDTO auditDTO) {
        return batchAuditComments(auditDTO.getCommentIds(), auditDTO.getStatus(), auditDTO.getRejectReason(), 0L);
    }

    @Override
    @Transactional
    public boolean batchAuditComments(List<Long> commentIds, Integer status, String rejectReason, Long operatorId) {
        if (status == 2 && !StringUtils.hasText(rejectReason)) {
            throw new GeneralBusinessException("拒绝时必须填写原因");
        }

        List<CommunityComment> commentsToUpdate = new ArrayList<>();
        for (Long commentId : commentIds) {
            CommunityComment comment = new CommunityComment();
            comment.setId(commentId);
            comment.setStatus(status);
            comment.setAdminRemark("操作人ID: " + operatorId);

            if (status == 2) {
                comment.setRejectReason(rejectReason);
            } else {
                comment.setRejectReason(null);
            }
            commentsToUpdate.add(comment);
        }

        // 修复：使用 commentMapper 进行批量更新
        // MybatisPlus 的 ServiceImpl.updateBatchById 实际上也是循环调用 updateById
        for (CommunityComment comment : commentsToUpdate) {
            commentMapper.updateById(comment);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long operatorId) {
        return batchDeleteComments(Collections.singletonList(commentId), operatorId);
    }

    @Override
    @Transactional
    public boolean batchDeleteComments(List<Long> commentIds, Long operatorId) {
        List<CommunityComment> commentsToUpdate = new ArrayList<>();
        for (Long commentId : commentIds) {
            CommunityComment comment = new CommunityComment();
            comment.setId(commentId);
            comment.setStatus(3); // 逻辑删除
            comment.setAdminRemark("删除操作人ID: " + operatorId);
            commentsToUpdate.add(comment);
        }

        // 修复：使用 commentMapper 进行批量更新
        for (CommunityComment comment : commentsToUpdate) {
            commentMapper.updateById(comment);
        }
        return true;
    }

    // ========================== 举报相关 ==========================

    @Override
    public IPage<ContentReport> getReportPage(ReportQueryDTO queryDTO) {
        Page<ContentReport> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return reportMapper.selectReportPage(page, queryDTO);
    }

    @Override
    public ContentReport getReportDetail(Long reportId) {
        ContentReport report = reportMapper.selectReportDetailById(reportId);
        if (report != null) {
            // 根据举报类型加载具体内容
            if (report.getContentType() == 1) { // 帖子
                report.setContentDetail(postMapper.selectPostDetailById(report.getContentId()));
            } else if (report.getContentType() == 2) { // 评论
                report.setContentDetail(commentMapper.selectById(report.getContentId()));
            }
        }
        return report;
    }

    @Override
    @Transactional
    public boolean handleReport(ReportHandleDTO handleDTO) {
        return batchHandleReports(handleDTO.getReportIds(), handleDTO.getStatus(), handleDTO.getHandleResult(), 0L);
    }

    @Override
    @Transactional
    public boolean batchHandleReports(List<Long> reportIds, Integer status, String handleResult, Long handlerAdminId) {
        List<ContentReport> reportsToUpdate = new ArrayList<>();
        for (Long reportId : reportIds) {
            ContentReport report = new ContentReport();
            report.setId(reportId);
            report.setStatus(status);
            report.setHandleResult(handleResult);
            report.setHandlerAdminId(handlerAdminId);
            report.setHandleTime(LocalDateTime.now());
            reportsToUpdate.add(report);
        }

        // 修复：使用 reportMapper 进行批量更新
        for (ContentReport report : reportsToUpdate) {
            reportMapper.updateById(report);
        }
        return true;
    }

    // ========================== 统计相关 ==========================

    @Override
    public ContentStatisticsVO getContentStatistics() {
        ContentStatisticsVO vo = new ContentStatisticsVO();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Map<String, Object> todayParams = new HashMap<>();
        todayParams.put("startTime", LocalDateTime.of(today, LocalTime.MIN));
        todayParams.put("endTime", LocalDateTime.of(today, LocalTime.MAX));

        Map<String, Object> yesterdayParams = new HashMap<>();
        yesterdayParams.put("startTime", LocalDateTime.of(yesterday, LocalTime.MIN));
        yesterdayParams.put("endTime", LocalDateTime.of(yesterday, LocalTime.MAX));

        // 帖子统计
        vo.setTodayPosts(postMapper.countByTimeRange(todayParams));
        vo.setYesterdayPosts(postMapper.countByTimeRange(yesterdayParams));
        vo.setTotalPosts(postMapper.selectCount(null));

        // 评论统计
        vo.setTodayComments(commentMapper.countByTimeRange(todayParams));
        vo.setYesterdayComments(commentMapper.countByTimeRange(yesterdayParams));
        vo.setTotalComments(commentMapper.selectCount(null));

        return vo;
    }

    @Override
    public ReportStatisticsVO getReportStatistics() {
        ReportStatisticsVO vo = new ReportStatisticsVO();
        vo.setPendingPosts(postMapper.selectCount(new LambdaQueryWrapper<CommunityPost>().eq(CommunityPost::getStatus, 0)));
        vo.setPendingComments(commentMapper.selectCount(new LambdaQueryWrapper<CommunityComment>().eq(CommunityComment::getStatus, 0)));
        vo.setPendingReports(reportMapper.selectCount(new LambdaQueryWrapper<ContentReport>().eq(ContentReport::getStatus, 0)));
        return vo;
    }

    // ========================== 导出功能 ==========================

    /**
     * 设置 Excel 导出响应头
     */
    private void setExcelResponseHeaders(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + encodedFileName + ".xlsx");
    }

    @Override
    public void exportPosts(PostQueryDTO queryDTO, HttpServletResponse response) {
        try {
            setExcelResponseHeaders(response, "社区帖子数据");
            List<CommunityPost> posts = postMapper.selectPostPage(new Page<>(1, -1), queryDTO).getRecords();
            EasyExcel.write(response.getOutputStream(), CommunityPost.class).sheet("帖子列表").doWrite(posts);
        } catch (IOException e) {
            log.error("导出帖子数据失败", e);
        }
    }

    @Override
    public void exportComments(CommentQueryDTO queryDTO, HttpServletResponse response) {
        try {
            setExcelResponseHeaders(response, "社区评论数据");
            List<CommunityComment> comments = commentMapper.selectCommentPage(new Page<>(1, -1), queryDTO).getRecords();
            EasyExcel.write(response.getOutputStream(), CommunityComment.class).sheet("评论列表").doWrite(comments);
        } catch (IOException e) {
            log.error("导出评论数据失败", e);
        }
    }

    @Override
    public void exportReports(ReportQueryDTO queryDTO, HttpServletResponse response) {
        try {
            setExcelResponseHeaders(response, "内容举报数据");
            List<ContentReport> reports = reportMapper.selectReportPage(new Page<>(1, -1), queryDTO).getRecords();
            EasyExcel.write(response.getOutputStream(), ContentReport.class).sheet("举报列表").doWrite(reports);
        } catch (IOException e) {
            log.error("导出举报数据失败", e);
        }
    }
}
