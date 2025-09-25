package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: AIProvider
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.enumerate
 * @Description: AI提供商枚举类，用于标识系统中可用的不同AI服务提供商。
 *               枚举值主要用于接口调用、日志记录以及配置选择等场景。
 */
public enum AIProvider {

    /** DeepSeek */
    DEEPSEEK("DeepSeek"),

    /** 智谱AI */
    ZHIPU_AI("ZhipuAI"),

    /** 阿里百炼 */
    ALIBABA_BAILAI("AliBaiLian"),

    /** Kimi */
    KIMI("Kimi");

    @EnumValue
    private final String value;

    AIProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}