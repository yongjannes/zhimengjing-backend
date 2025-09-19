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
        super(resultEnum.getMessage()); // 将默认消息传给父类
        this.resultEnum = resultEnum;
    }
    public PasswordErrorException(String dynamicMessage) {
        super(dynamicMessage); // 将我们动态生成的消息传给父类
        this.resultEnum = ResultEnum.PASSWORD_ERROR; // 关联基础错误类型
    }
}
