package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: UserGrowthPredictionVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 用户增长预测数据视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGrowthPredictionVO {

    /**
     * 预测日期
     */
    private LocalDate date;

    /**
     * 预测用户数
     */
    private BigDecimal predictedUsers;

    /**
     * 置信度 (0-1之间)
     */
    private BigDecimal confidence;

    /**
     * 预测模型类型
     */
    private String modelType;
}
