package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: DreamCategoryStatistics
 * @Author: 殇枫
 * @Package: com.dreamanalysis.admin.entity
 * @description: 梦境分类统计实体类，对应表 dream_categories_statistics，
 *               用于存储分类相关的统计信息，如梦境数量、平均睡眠质量等。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_categories_statistics")
public class DreamCategoryStatistics extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long  id;

    /**
     * 分类ID（关联 dream_categories 表）
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 总梦境数
     */
    @TableField("total_dreams")
    private Integer totalDreams;

    /**
     * 公开梦境数
     */
    @TableField("public_dreams")
    private Integer publicDreams;

    /**
     * 私有梦境数
     */
    @TableField("private_dreams")
    private Integer privateDreams;

    /**
     * 已审核梦境数
     */
    @TableField("approved_dreams")
    private Integer approvedDreams;

    /**
     * 待审核梦境数
     */
    @TableField("pending_dreams")
    private Integer pendingDreams;

    /**
     * 已拒绝梦境数
     */
    @TableField("rejected_dreams")
    private Integer rejectedDreams;

    /**
     * 平均睡眠质量（评分或指数）
     */
    @TableField("avg_sleep_quality")
    private BigDecimal avgSleepQuality;

    /**
     * 平均清醒梦程度（评分或指数）
     */
    @TableField("avg_lucidity_level")
    private BigDecimal avgLucidityLevel;

    /**
     * 最后统计计算时间
     */
    @TableField("last_calculated")
    private LocalDateTime lastCalculated;

    /**
     * 所属分类对象（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private DreamCategory category;
}
