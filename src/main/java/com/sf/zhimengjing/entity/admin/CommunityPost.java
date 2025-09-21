package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: CommunityPost
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.community
 * @description: 社区帖子实体类，对应数据库表 community_posts
 *              用于存储社区用户发布的帖子及相关信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_posts")
public class CommunityPost extends BaseEntity {

    /**
     * 帖子ID，自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发帖用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 帖子标题
     */
    @TableField("title")
    private String title;

    /**
     * 帖子内容（富文本/HTML）
     */
    @TableField("content")
    private String content;

    /**
     * 帖子纯文本内容，用于搜索或摘要
     */
    @TableField("content_text")
    private String contentText;

    /**
     * 帖子图片信息，存储格式可为 JSON 或逗号分隔
     */
    @TableField("images")
    private String images;

    /**
     * 帖子标签，存储格式可为 JSON 或逗号分隔
     */
    @TableField("tags")
    private String tags;

    /**
     * 所属分类ID
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 分享数
     */
    @TableField("share_count")
    private Integer shareCount;

    /**
     * 收藏数
     */
    @TableField("collect_count")
    private Integer collectCount;

    /**
     * 是否置顶，true-置顶，false-非置顶
     */
    @TableField("is_top")
    private Boolean isTop;

    /**
     * 是否热门，true-热门，false-非热门
     */
    @TableField("is_hot")
    private Boolean isHot;

    /**
     * 是否匿名发帖，true-匿名，false-实名
     */
    @TableField("is_anonymous")
    private Boolean isAnonymous;

    /**
     * 帖子状态
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

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /**
     * 最近评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_commented_at")
    private LocalDateTime lastCommentedAt;

    // ---------------- 以下字段非数据库字段，仅用于展示或业务逻辑 ----------------

    /**
     * 发帖用户昵称
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 发帖用户头像URL
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 帖子评论列表
     */
    @TableField(exist = false)
    private List<CommunityComment> comments;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 图片列表（解析自 images 字段）
     */
    @TableField(exist = false)
    private List<String> imageList;

    /**
     * 标签列表（解析自 tags 字段）
     */
    @TableField(exist = false)
    private List<String> tagList;
}
