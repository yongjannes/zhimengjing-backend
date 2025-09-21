package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ContentReport
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.community
 * @description: 内容举报实体类，对应数据库表 content_reports
 *              用于存储用户对帖子或评论的举报信息及处理情况
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("content_reports")
public class ContentReport extends BaseEntity {

    /**
     * 举报ID，自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 举报用户ID
     */
    @TableField("reporter_user_id")
    private Long reporterUserId;

    /**
     * 被举报内容类型
     * 1-帖子，2-评论
     */
    @TableField("content_type")
    private Integer contentType;

    /**
     * 被举报内容ID（帖子ID或评论ID）
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 被举报用户ID
     */
    @TableField("reported_user_id")
    private Long reportedUserId;

    /**
     * 举报类型
     * 1-违规内容，2-恶意行为，3-其他（可自定义）
     */
    @TableField("report_type")
    private Integer reportType;

    /**
     * 举报理由/说明
     */
    @TableField("report_reason")
    private String reportReason;

    /**
     * 证据图片，存储格式可为 JSON 或逗号分隔
     */
    @TableField("evidence_images")
    private String evidenceImages;

    /**
     * 处理状态
     * 0-待处理，1-已处理，2-驳回
     */
    @TableField("status")
    private Integer status;

    /**
     * 处理该举报的管理员ID
     */
    @TableField("handler_admin_id")
    private Long handlerAdminId;

    /**
     * 处理结果说明
     */
    @TableField("handle_result")
    private String handleResult;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("handle_time")
    private LocalDateTime handleTime;

    // ---------------- 以下字段非数据库字段，仅用于展示或业务逻辑 ----------------

    /**
     * 举报用户昵称，用于展示
     */
    @TableField(exist = false)
    private String reporterNickname;

    /**
     * 被举报用户昵称，用于展示
     */
    @TableField(exist = false)
    private String reportedNickname;

    /**
     * 处理管理员姓名，用于展示
     */
    @TableField(exist = false)
    private String handlerAdminName;

    /**
     * 被举报内容详情，可为 CommunityPost 或 CommunityComment 对象
     */
    @TableField(exist = false)
    private Object contentDetail;

    /**
     * 证据图片列表（解析自 evidenceImages 字段）
     */
    @TableField(exist = false)
    private List<String> evidenceImageList;
}
