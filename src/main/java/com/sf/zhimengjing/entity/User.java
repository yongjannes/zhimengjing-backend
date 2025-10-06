package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: User
 * @Author 殇枫
 * @Package com.sf.zhimengjing.entity
 * @description: 用户表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {
    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 密码 */
    private String password;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 性别:0-未知,1-男,2-女 */
    private Integer gender;

    /** 生日 */
    private LocalDateTime birthday;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区县 */
    private String district;

    /** 详细地址 */
    private String address;

    /** 个性签名 */
    private String signature;

    /** 用户等级:1-普通,2-VIP,3-高级VIP */
    private Integer userLevel;

    /** 用户类型:1-普通用户,2-企业用户 */
    private Integer userType;

    /** 状态:0-禁用,1-正常,2-待审核 */
    private Integer status;

    /** 注册来源:web,ios,android,miniapp */
    private String registerSource;

    /** 注册IP */
    private String registerIp;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 登录次数 */
    private Integer loginCount;

    /** 总积分 */
    private Integer totalPoints;

    /** 可用积分 */
    private Integer availablePoints;

    /** 账户余额 */
    private BigDecimal balance;

    /** 累计消费 */
    private BigDecimal totalConsume;

    /** 是否实名认证:0-否,1-是 */
    private Integer isVerified;

    /** 实名认证姓名 */
    private String verifiedName;

    /** 实名认证身份证号 */
    private String verifiedCard;

    /** 实名认证时间 */
    private LocalDateTime verifiedTime;

    /** 邀请码 */
    private String inviteCode;

    /** 邀请人ID */
    private Long invitedBy;

    /** 微信openid */
    private String wechatOpenid;

    /** 微信unionid */
    private String wechatUnionid;

    /** QQ openid */
    private String qqOpenid;

    /** 微博uid */
    private String weiboUid;

    /** 删除标记:0-正常,1-已删除 */
    @TableLogic
    private Integer deleteFlag;

    /** 创建人ID */
    private Long createBy;

    /** 更新人ID */
    private Long updateBy;
}
