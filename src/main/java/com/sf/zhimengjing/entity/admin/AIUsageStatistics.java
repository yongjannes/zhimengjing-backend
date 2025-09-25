package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: AIUsageStatistics
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: AI使用统计实体类，用于存储AI模型使用的统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_usage_statistics")
public class AIUsageStatistics extends BaseEntity {

    /** 统计日期 */
    private LocalDate statDate;

    /** 模型编码 */
    private String modelCode;

    /** 总请求数 */
    private Integer totalRequests;

    /** 成功请求数 */
    private Integer successRequests;

    /** 失败请求数 */
    private Integer failedRequests;

    /** 总令牌数 */
    private Long totalTokens;

    /** 总成本 */
    private BigDecimal totalCost;

    /** 平均响应时间（毫秒） */
    private Integer avgResponseTime;
}