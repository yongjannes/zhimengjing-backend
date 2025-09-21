package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.*;
import com.sf.zhimengjing.common.model.vo.ContentStatisticsVO;
import com.sf.zhimengjing.common.model.vo.ReportStatisticsVO;
import com.sf.zhimengjing.entity.admin.CommunityComment;
import com.sf.zhimengjing.entity.admin.CommunityPost;
import com.sf.zhimengjing.entity.admin.ContentReport;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Title: CommunityContentService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.community
 * @description: 社区内容管理服务接口
 * 提供帖子、评论、举报的查询、审核、删除、统计及导出等功能
 */
public interface CommunityContentService extends IService<CommunityPost> {

    /**
     * 分页查询帖子列表
     * @param queryDTO 查询条件 DTO
     * @return 分页后的帖子列表
     */
    IPage<CommunityPost> getPostPage(PostQueryDTO queryDTO);

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @return 帖子对象，包括作者信息和分类信息
     */
    CommunityPost getPostDetail(Long postId);

    /**
     * 审核单个帖子
     * @param auditDTO 帖子审核 DTO
     * @return 审核是否成功
     */
    boolean auditPost(PostAuditDTO auditDTO);

    /**
     * 批量审核帖子
     * @param postIds 帖子ID列表
     * @param status 审核状态（1-通过，2-拒绝）
     * @param rejectReason 拒绝原因（status=2时必填）
     * @param operatorId 操作管理员ID
     * @return 审核是否成功
     */
    boolean batchAuditPosts(List<Long> postIds, Integer status, String rejectReason, Long operatorId);

    /**
     * 更新帖子信息
     * @param updateDTO 帖子更新 DTO
     * @return 更新是否成功
     */
    boolean updatePost(PostUpdateDTO updateDTO);

    /**
     * 删除帖子
     * @param postId 帖子ID
     * @param operatorId 操作管理员ID
     * @return 删除是否成功
     */
    boolean deletePost(Long postId, Long operatorId);

    /**
     * 批量删除帖子
     * @param postIds 帖子ID列表
     * @param operatorId 操作管理员ID
     * @return 删除是否成功
     */
    boolean batchDeletePosts(List<Long> postIds, Long operatorId);

    /**
     * 设置帖子置顶状态
     * @param postId 帖子ID
     * @param isTop 是否置顶
     * @param operatorId 操作管理员ID
     * @return 操作是否成功
     */
    boolean togglePostTop(Long postId, Boolean isTop, Long operatorId);

    /**
     * 设置帖子热门状态
     * @param postId 帖子ID
     * @param isHot 是否热门
     * @param operatorId 操作管理员ID
     * @return 操作是否成功
     */
    boolean togglePostHot(Long postId, Boolean isHot, Long operatorId);

    /**
     * 分页查询评论列表
     * @param queryDTO 查询条件 DTO
     * @return 分页后的评论列表
     */
    IPage<CommunityComment> getCommentPage(CommentQueryDTO queryDTO);

    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @return 评论对象，包括作者信息和所属帖子标题
     */
    CommunityComment getCommentDetail(Long commentId);

    /**
     * 审核单个评论
     * @param auditDTO 评论审核 DTO
     * @return 审核是否成功
     */
    boolean auditComment(CommentAuditDTO auditDTO);

    /**
     * 批量审核评论
     * @param commentIds 评论ID列表
     * @param status 审核状态（1-通过，2-拒绝）
     * @param rejectReason 拒绝原因（status=2时必填）
     * @param operatorId 操作管理员ID
     * @return 审核是否成功
     */
    boolean batchAuditComments(List<Long> commentIds, Integer status, String rejectReason, Long operatorId);

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param operatorId 操作管理员ID
     * @return 删除是否成功
     */
    boolean deleteComment(Long commentId, Long operatorId);

    /**
     * 批量删除评论
     * @param commentIds 评论ID列表
     * @param operatorId 操作管理员ID
     * @return 删除是否成功
     */
    boolean batchDeleteComments(List<Long> commentIds, Long operatorId);

    /**
     * 分页查询举报列表
     * @param queryDTO 查询条件 DTO
     * @return 分页后的举报列表
     */
    IPage<ContentReport> getReportPage(ReportQueryDTO queryDTO);

    /**
     * 获取举报详情
     * @param reportId 举报ID
     * @return 举报对象，包括举报人、被举报人及处理管理员信息
     */
    ContentReport getReportDetail(Long reportId);

    /**
     * 处理单个举报
     * @param handleDTO 举报处理 DTO
     * @return 处理是否成功
     */
    boolean handleReport(ReportHandleDTO handleDTO);

    /**
     * 批量处理举报
     * @param reportIds 举报ID列表
     * @param status 处理状态（1-已处理，2-已驳回）
     * @param handleResult 处理结果描述
     * @param handlerAdminId 操作管理员ID
     * @return 处理是否成功
     */
    boolean batchHandleReports(List<Long> reportIds, Integer status, String handleResult, Long handlerAdminId);

    /**
     * 获取帖子和评论统计信息
     * @return ContentStatisticsVO 对象，包含今日、昨日及总数统计
     */
    ContentStatisticsVO getContentStatistics();

    /**
     * 获取待处理统计信息
     * @return ReportStatisticsVO 对象，包含待审核帖子、评论及待处理举报数量
     */
    ReportStatisticsVO getReportStatistics();

    /**
     * 导出帖子数据
     * @param queryDTO 查询条件 DTO
     * @param response HTTP 响应对象
     */
    void exportPosts(PostQueryDTO queryDTO, HttpServletResponse response);

    /**
     * 导出评论数据
     * @param queryDTO 查询条件 DTO
     * @param response HTTP 响应对象
     */
    void exportComments(CommentQueryDTO queryDTO, HttpServletResponse response);

    /**
     * 导出举报数据
     * @param queryDTO 查询条件 DTO
     * @param response HTTP 响应对象
     */
    void exportReports(ReportQueryDTO queryDTO, HttpServletResponse response);
}
