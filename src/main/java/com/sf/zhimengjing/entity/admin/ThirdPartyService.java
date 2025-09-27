package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: ThirdPartyService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 第三方服务配置实体类，对应数据库表 third_party_services
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("third_party_services")
public class ThirdPartyService extends BaseEntity {

    /** 服务名称 */
    private String serviceName;

    /** 服务类型 */
    private String serviceType;

    /** 配置数据(JSON格式) */
    private String configData;

    /** 是否激活 */
    private Boolean isActive;

    /** 优先级 */
    private Integer priority;

    /** 描述 */
    private String description;

    /** 最后测试时间 */
    private LocalDateTime lastTestTime;

    /** 测试结果 */
    private String testResult;

    /** 测试错误信息 */
    private String testError;

    /** 删除标志（逻辑删除） */
    @TableLogic
    private Integer deleteFlag;
}