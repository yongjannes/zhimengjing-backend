package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

/**
 * @Title: CommunityComment
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.community
 * @description: 社区评论实体类，对应数据库表 community_comments
 *              用于存储用户对帖子或其他评论的评论信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_comments")
public class CommunityComment extends BaseEntity {

    /**
     * 评论ID，自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属帖子ID
     */
    @TableField("post_id")
    private Long postId;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 父评论ID（用于回复某条评论）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 回复对象用户ID（针对某条评论的回复）
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /**
     * 评论内容（富文本/HTML）
     */
    @TableField("content")
    private String content;

    /**
     * 评论纯文本内容，用于搜索或摘要
     */
    @TableField("content_text")
    private String contentText;

    /**
     * 评论图片信息，存储格式可为 JSON 或逗号分隔
     */
    @TableField("images")
    private String images;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数
     */
    @TableField("reply_count")
    private Integer replyCount;

    /**
     * 是否匿名评论，true-匿名，false-实名
     */
    @TableField("is_anonymous")
    private Boolean isAnonymous;

    /**
     * 评论状态
     * 0-待审核，1-已通过，2-已拒绝
     */
    @TableField("status")
    private Integer status;

    /**
     * 审核拒绝原因
     */
    @TableField("reject_reason")
    private String rejectReason;

    /**
     * 管理员备注
     */
    @TableField("admin_remark")
    private String adminRemark;

    // ---------------- 以下字段非数据库字段，仅用于展示或业务逻辑 ----------------

    /**
     * 评论用户昵称
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 评论用户头像URL
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 所属帖子标题，用于展示
     */
    @TableField(exist = false)
    private String postTitle;

    /**
     * 回复对象昵称，用于展示
     */
    @TableField(exist = false)
    private String replyToNickname;

    /**
     * 评论图片列表（解析自 images 字段）
     */
    @TableField(exist = false)
    private List<String> imageList;
}
