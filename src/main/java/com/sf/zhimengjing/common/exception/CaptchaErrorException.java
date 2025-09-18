package com.sf.zhimengjing.common.exception;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * @Title: CaptchaErrorException
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.exception
 * @description: 验证码错误异常
 */
@Getter
public class CaptchaErrorException extends RuntimeException{
    private final ResultEnum resultEnum;

    public CaptchaErrorException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }
}
