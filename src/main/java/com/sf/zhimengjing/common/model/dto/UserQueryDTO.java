package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: UserQueryDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 用户列表查询参数 DTO
 */
@Data
@Schema(description = "用户列表查询DTO")
public class UserQueryDTO {

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 用户状态:0-禁用,1-正常,2-待审核 */
    @Schema(description = "用户状态:0-禁用,1-正常,2-待审核")
    private Integer status;

    /** 用户等级:1-普通,2-VIP,3-高级VIP */
    @Schema(description = "用户等级:1-普通,2-VIP,3-高级VIP")
    private Integer userLevel;

    /** 注册开始时间 */
    @Schema(description = "注册开始时间")
    private LocalDateTime registerStartTime;

    /** 注册结束时间 */
    @Schema(description = "注册结束时间")
    private LocalDateTime registerEndTime;

    /** 页码 */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页大小 */
    @Schema(description = "每页大小", defaultValue = "10")
    private Integer pageSize = 10;
}
