package com.sf.zhimengjing.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Title: GeneralBusinessException
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.exception
 * @description: 处理通用的业务异常
 */
@Getter
@Setter
public class GeneralBusinessException extends  RuntimeException {


    public GeneralBusinessException(String message) {
        super(message);
    }
}
