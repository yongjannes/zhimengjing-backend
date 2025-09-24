package com.sf.zhimengjing.entity.analytics;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: DreamContentAnalytics
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.analytics
 * @Description: 梦境内容维度表实体类，用于存储梦境内容的分析数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dim_dream_content")
public class DreamContentAnalytics extends BaseEntity {


    /** 用户ID */
    private Long userId;

    /** 梦境内容文本 */
    private String contentText;

    /** 情感分数（-1.0到1.0） */
    private BigDecimal emotionScore;

    /** 情感标签（positive-积极，negative-消极，neutral-中性） */
    private String emotionLabel;

    /** 关键词列表（JSON格式） */
    private String keywordList;

    /** 分类列表（JSON格式） */
    private String categoryList;

    /** 内容长度 */
    private Integer contentLength;

    /** 图片数量 */
    private Integer imageCount;

    /** 创建日期 */
    private LocalDate createdDate;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}