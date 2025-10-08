package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Title: DreamRecord
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.entity
 * @Description: 梦境记录实体类，存储用户梦境的详细信息及状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_records")
public class DreamRecord extends BaseEntity {

    /** 用户ID，关联用户表 */
    private Long userId;

    /** 梦境标题 */
    private String title;

    /** 梦境内容 */
    private String content;

    /** 梦境分类ID，关联梦境分类表 */
    private Integer categoryId;

    /** 做梦前的心情 */
    private String moodBefore;

    /** 做梦后的心情 */
    private String moodAfter;

    /** 睡眠质量评分（例如 1-5） */
    private Integer sleepQuality;

    /** 梦境发生的日期 */
    private LocalDate dreamDate;

    /** 记录创建时间 */
    private LocalDateTime recordDate;

    /** 清醒梦程度评分（清晰度等级） */
    private Integer lucidityLevel;

    /** 是否为重复梦（0-否，1-是） */
    private Integer isRecurring;

    /** 重复梦的出现次数 */
    private Integer recurringCount;

    /** 梦境状态（0-未审核/草稿，1-已审核/公开等） */
    private Integer status;

    /** 审核备注 */
    private String reviewNotes;

    /** 审核人ID */
    private Long reviewerId;

    /** 审核时间 */
    private LocalDateTime reviewedAt;

    /** 浏览次数 */
    private Integer viewCount;

    /** 点赞次数 */
    private Integer likeCount;

    /** 分享次数 */
    private Integer shareCount;

    /** 是否公开（0-否，1-是） */
    private Integer isPublic;

    /** 是否匿名（0-否，1-是） */
    private Integer isAnonymous;

    /** 删除标记（0-未删除，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}
