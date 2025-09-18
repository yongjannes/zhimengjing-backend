package com.sf.zhimengjing.common.exception;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * @Title: AccountNotFoundException
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate.ResultEnum
 * @description: 账户不存在异常
 */
@Getter
public class AccountNotFoundException extends RuntimeException {

    private final ResultEnum resultEnum;

    public AccountNotFoundException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }

}
