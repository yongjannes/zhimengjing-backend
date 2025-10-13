package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: CommunityPostListVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 社区帖子列表VO
 */
@Data
@Schema(description = "社区帖子列表VO")
public class CommunityPostListVO {

    /** 帖子ID */
    @Schema(description = "帖子ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 用户ID */
    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /** 用户名 */
    @Schema(description = "用户名")
    private String userName;

    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String userNickname;

    /** 用户头像 */
    @Schema(description = "用户头像")
    private String userAvatar;

    /** 帖子标题 */
    @Schema(description = "帖子标题")
    private String title;

    /** 帖子内容 */
    @Schema(description = "帖子内容")
    private String content;

    /** 纯文本内容 */
    @Schema(description = "纯文本内容")
    private String contentText;

    /** 图片URL列表（JSON格式） */
    @Schema(description = "图片URL列表（JSON格式）")
    private String images;

    /** 标签列表（JSON格式） */
    @Schema(description = "标签列表（JSON格式）")
    private String tags;

    /** 分类ID */
    @Schema(description = "分类ID")
    private Integer categoryId;

    /** 分类名称 */
    @Schema(description = "分类名称")
    private String categoryName;

    /** 查看次数 */
    @Schema(description = "查看次数")
    private Integer viewCount;

    /** 点赞数 */
    @Schema(description = "点赞数")
    private Integer likeCount;

    /** 评论数 */
    @Schema(description = "评论数")
    private Integer commentCount;

    /** 分享数 */
    @Schema(description = "分享数")
    private Integer shareCount;

    /** 收藏数 */
    @Schema(description = "收藏数")
    private Integer collectCount;

    /** 是否置顶:0-否,1-是 */
    @Schema(description = "是否置顶:0-否,1-是")
    private Integer isTop;

    /** 是否热门:0-否,1-是 */
    @Schema(description = "是否热门:0-否,1-是")
    private Integer isHot;

    /** 是否匿名:0-否,1-是 */
    @Schema(description = "是否匿名:0-否,1-是")
    private Integer isAnonymous;

    /** 状态：0-待审核，1-已通过，2-已拒绝，3-已删除 */
    @Schema(description = "状态:0-待审核,1-已通过,2-已拒绝,3-已删除")
    private Integer status;

    /** 状态文本 */
    @Schema(description = "状态文本")
    private String statusText;

    /** 拒绝原因 */
    @Schema(description = "拒绝原因")
    private String rejectReason;

    /** 管理员备注 */
    @Schema(description = "管理员备注")
    private String adminRemark;

    /** 发布时间 */
    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishedAt;

    /** 最后评论时间 */
    @Schema(description = "最后评论时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastCommentedAt;

    /** 创建时间 */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}