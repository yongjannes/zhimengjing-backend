package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.ReportReviewDTO;
import com.sf.zhimengjing.entity.admin.ReportReview;
import java.time.LocalDate;

/**
 * @Title: ReportReviewService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 报告审核服务接口，提供报告审核相关业务操作
 */
public interface ReportReviewService extends IService<ReportReview> {

    /** 获取待审核报告列表（分页） */
    IPage<ReportReviewDTO> getPendingReviews(Page<ReportReviewDTO> page, Long reviewerId);

    /** 获取单条审核记录详情 */
    ReportReviewDTO getReviewDetail(Long reviewId);

    /** 提交审核结果 */
    boolean submitReview(ReportReviewDTO.ReviewRequestDTO requestDTO, Long reviewerId);

    /** 批量分配审核任务 */
    boolean assignReviewTasks(ReportReviewDTO.BatchAssignDTO assignDTO, Long operatorId);

    /** 用户提交申诉 */
    boolean submitAppeal(Long reviewId, String appealReason, Long userId);

    /** 管理员/审核员处理申诉 */
    boolean handleAppeal(Long reviewId, String appealResult, String comment, Long handlerId);

    /** 获取审核统计信息（按时间范围和审核员） */
    ReportReviewDTO.ReviewStatsVO getReviewStats(Long reviewerId, LocalDate startDate, LocalDate endDate);

    /** 自动审核检查（如违规检测） */
    ReportReview autoReviewCheck(Long reportId);

    /** 获取审核员的审核记录列表（分页） */
    IPage<ReportReviewDTO> getMyReviews(Page<ReportReviewDTO> page, Long reviewerId);

    /** 根据报告ID获取对应审核记录 */
    ReportReviewDTO getReviewByReportId(Long reportId);
}
