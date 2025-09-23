package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Title: VipLevel
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: VIP等级实体类，用于存储VIP会员等级配置信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vip_levels")
public class VipLevel extends BaseEntity {

    /** 等级编码 */
    private String levelCode;

    /** 等级名称 */
    private String levelName;

    /** 等级排序 */
    private Integer levelOrder;

    /** 月费价格 */
    private BigDecimal monthlyPrice;

    /** 季费价格 */
    private BigDecimal quarterlyPrice;

    /** 年费价格 */
    private BigDecimal yearlyPrice;

    /** 折扣率 */
    private BigDecimal discountRate;

    /** 每日解析次数限制 */
    private Integer dailyAnalysisLimit;

    /** 存储空间(MB) */
    private Integer storageSpaceMb;

    /** 可访问的AI模型(JSON) */
    private String aiModelAccess;

    /** 优先客服支持 */
    private Boolean prioritySupport;

    /** 无广告体验 */
    private Boolean adFree;

    /** 高级功能列表(JSON) */
    private String advancedFeatures;

    /** 等级描述 */
    private String description;

    /** 是否启用 */
    private Boolean isActive;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}