package com.sf.zhimengjing.entity.analytics;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: PlatformStatistics
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.analytics
 * @Description: 平台统计实体类，用于存储平台运营的统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("platform_statistics")
public class PlatformStatistics extends BaseEntity {


    /** 统计日期 */
    private LocalDate statDate;

    /** 总用户数 */
    private Integer totalUsers;

    /** 活跃用户数 */
    private Integer activeUsers;

    /** 新增用户数 */
    private Integer newUsers;

    /** 总梦境数 */
    private Integer totalDreams;

    /** 新增梦境数 */
    private Integer newDreams;

    /** 总分享数 */
    private Integer totalShares;

    /** VIP用户数 */
    private Integer vipUsers;

    /** 收入 */
    private BigDecimal revenue;

    /** 转化率 */
    private BigDecimal conversionRate;

    /** 留存率 */
    private BigDecimal retentionRate;

    /** 平均会话时长 */
    private Integer avgSessionDuration;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}