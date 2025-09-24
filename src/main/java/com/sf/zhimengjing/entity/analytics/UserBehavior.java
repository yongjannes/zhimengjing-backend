package com.sf.zhimengjing.entity.analytics;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.entity.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Title: UserBehavior
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.analytics
 * @Description: 用户行为事实表实体类，用于存储用户在平台上的各种行为数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fact_user_behavior")
public class UserBehavior extends BaseEntity {


    /** 用户ID */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 行为类型（如：PAGE_VIEW-页面浏览，DREAM_SUBMIT-提交梦境，LOGIN-登录等） */
    @NotNull(message = "行为类型不能为空")
    private String behaviorType;

    /** 行为发生时间 */
    @NotNull(message = "行为发生时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime behaviorTime;

    /** 会话ID */
    private String sessionId;

    /** 页面路径 */
    private String pagePath;

    /** 停留时长（秒） */
    private Integer stayDuration;

    /** 设备类型（mobile-移动端，desktop-桌面端） */
    private String deviceType;

    /** 操作系统类型 */
    private String osType;

    /** 应用版本 */
    private String appVersion;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}