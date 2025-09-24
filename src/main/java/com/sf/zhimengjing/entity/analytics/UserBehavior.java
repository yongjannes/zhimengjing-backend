package com.sf.zhimengjing.entity.analytics;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
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
    private Long userId;

    /** 行为类型（如：PAGE_VIEW-页面浏览，DREAM_SUBMIT-提交梦境，LOGIN-登录等） */
    private String behaviorType;

    /** 行为发生时间 */
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