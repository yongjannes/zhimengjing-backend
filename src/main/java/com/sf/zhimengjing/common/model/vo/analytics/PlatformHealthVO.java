package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: PlatformHealthVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 平台健康度评估视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformHealthVO {

    /**
     * 总体健康分数 (0-100)
     */
    private Integer overallScore;

    /**
     * 健康等级 (excellent/good/fair/poor/critical)
     */
    private String healthLevel;

    /**
     * 用户增长分数
     */
    private Integer userGrowthScore;

    /**
     * 用户留存分数
     */
    private Integer retentionScore;

    /**
     * 收入表现分数
     */
    private Integer revenueScore;

    /**
     * 系统性能分数
     */
    private Integer performanceScore;

    /**
     * 改进建议列表
     */
    private List<String> recommendations;

    /**
     * 健康风险列表
     */
    private List<HealthRiskVO> risks;

    /**
     * 最后评估时间
     */
    private LocalDateTime lastAssessmentTime;
}
