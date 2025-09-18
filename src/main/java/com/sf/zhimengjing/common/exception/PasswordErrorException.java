package com.sf.zhimengjing.common.exception;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * @Title: PasswordErrorException
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 密码错误异常
 */
@Getter
public class PasswordErrorException extends  RuntimeException{
    private final ResultEnum resultEnum;

    public PasswordErrorException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }
}
