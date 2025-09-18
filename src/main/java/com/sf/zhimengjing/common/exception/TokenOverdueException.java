package com.sf.zhimengjing.common.exception;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import lombok.Getter;

/**
 * @Title: TokenOverdueException
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 令牌过期异常
 */
@Getter
public class TokenOverdueException extends RuntimeException {
    private final ResultEnum resultEnum;
    public TokenOverdueException(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }
}