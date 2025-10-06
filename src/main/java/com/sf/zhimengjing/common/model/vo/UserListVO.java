package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: UserListVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户列表返回 VO
 */
@Data
@Schema(description = "用户列表VO")
public class UserListVO {

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 昵称 */
    @Schema(description = "昵称")
    private String nickname;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 头像URL */
    @Schema(description = "头像URL")
    private String avatar;

    /** 性别:0-未知,1-男,2-女 */
    @Schema(description = "性别:0-未知,1-男,2-女")
    private Integer gender;

    /** 性别文本 */
    @Schema(description = "性别文本")
    private String genderText;

    /** 生日 */
    @Schema(description = "生日")
    private LocalDateTime birthday;

    /** 省份 */
    @Schema(description = "省份")
    private String province;

    /** 城市 */
    @Schema(description = "城市")
    private String city;

    /** 用户等级:1-普通,2-VIP,3-高级VIP */
    @Schema(description = "用户等级:1-普通,2-VIP,3-高级VIP")
    private Integer userLevel;

    /** 用户等级文本 */
    @Schema(description = "用户等级文本")
    private String userLevelText;

    /** 用户类型:1-普通用户,2-企业用户 */
    @Schema(description = "用户类型:1-普通用户,2-企业用户")
    private Integer userType;

    /** 用户状态:0-禁用,1-正常,2-待审核 */
    @Schema(description = "用户状态:0-禁用,1-正常,2-待审核")
    private Integer status;

    /** 用户状态文本 */
    @Schema(description = "用户状态文本")
    private String statusText;

    /** 注册来源 */
    @Schema(description = "注册来源")
    private String registerSource;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    /** 登录次数 */
    @Schema(description = "登录次数")
    private Integer loginCount;

    /** 可用积分 */
    @Schema(description = "可用积分")
    private Integer availablePoints;

    /** 账户余额 */
    @Schema(description = "账户余额")
    private BigDecimal balance;

    /** 累计消费 */
    @Schema(description = "累计消费")
    private BigDecimal totalConsume;

    /** 是否实名认证:0-否,1-是 */
    @Schema(description = "是否实名认证:0-否,1-是")
    private Integer isVerified;

    /** 实名认证文本 */
    @Schema(description = "实名认证文本")
    private String verifiedText;

    /** 邀请码 */
    @Schema(description = "邀请码")
    private String inviteCode;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 用户标签列表 */
    @Schema(description = "用户标签列表")
    private List<UserTagVO> tags;
}
