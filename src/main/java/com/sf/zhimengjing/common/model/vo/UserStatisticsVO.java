package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: UserStatisticsVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户统计 VO
 */
@Data
@Schema(description = "用户统计VO")
public class UserStatisticsVO {

    /** 总用户数 */
    @Schema(description = "总用户数")
    private Long totalUsers;

    /** 正常用户数 */
    @Schema(description = "正常用户数")
    private Long normalUsers;

    /** 禁用用户数 */
    @Schema(description = "禁用用户数")
    private Long disabledUsers;

    /** 待审核用户数 */
    @Schema(description = "待审核用户数")
    private Long pendingUsers;

    /** 今日新增用户数 */
    @Schema(description = "今日新增用户数")
    private Long todayNewUsers;

    /** 本周新增用户数 */
    @Schema(description = "本周新增用户数")
    private Long weekNewUsers;

    /** 本月新增用户数 */
    @Schema(description = "本月新增用户数")
    private Long monthNewUsers;

    /** 实名认证用户数 */
    @Schema(description = "实名认证用户数")
    private Long verifiedUsers;

    /** VIP用户数 */
    @Schema(description = "VIP用户数")
    private Long vipUsers;
}
