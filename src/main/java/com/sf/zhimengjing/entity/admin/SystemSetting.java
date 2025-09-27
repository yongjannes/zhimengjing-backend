package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: SystemSetting
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 系统配置实体类，对应数据库表 system_settings
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_settings")
public class SystemSetting extends BaseEntity {

    /** 配置键名 */
    private String settingKey;

    /** 配置值 */
    private String settingValue;

    /** 配置类型 */
    private String settingType;

    /** 配置分类 */
    private String category;

    /** 配置描述 */
    private String description;

    /** 是否加密 */
    private Boolean isEncrypted;

    /** 是否系统配置 */
    private Boolean isSystem;

    /** 删除标志（逻辑删除） */
    @TableLogic
    private Integer deleteFlag;
}