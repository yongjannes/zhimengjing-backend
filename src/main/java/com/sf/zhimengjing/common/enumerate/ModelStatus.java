package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: ModelStatus
 * @Description: 模型状态枚举类，用于标识机器学习模型的不同生命周期阶段。
 *               在数据库中会存储对应的中文描述（通过 @EnumValue 注解实现）。
 */
public enum ModelStatus {

    /** 模型正在训练中 */
    TRAINING("训练中"),

    /** 模型训练已完成 */
    COMPLETED("已完成"),

    /** 模型已部署，可对外提供服务 */
    DEPLOYED("已部署"),

    /** 模型已停用，不再对外提供服务 */
    DISABLED("已停用");

    /**
     * 数据库存储的值。
     * MyBatis-Plus 持久化时会使用该字段值，而不是枚举名。
     */
    @EnumValue
    private final String value;

    /**
     * 构造函数，绑定枚举常量对应的数据库值
     * @param value 中文描述
     */
    ModelStatus(String value) {
        this.value = value;
    }

    /**
     * 获取枚举对应的中文描述
     * @return 中文字符串
     */
    public String getValue() {
        return value;
    }
}
