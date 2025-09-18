package com.sf.zhimengjing.common.exception;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * @Title: AccountForbiddenException
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.exception
 * @description: 用户被封禁异常
 */
@Getter
public class AccountForbiddenException extends RuntimeException {
    private final ResultEnum resultEnum;

    public AccountForbiddenException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }
}