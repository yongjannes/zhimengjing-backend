package com.sf.zhimengjing.exception.handle;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import com.sf.zhimengjing.common.exception.*;
import com.sf.zhimengjing.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Title: GlobalExceptionHandler
 * @Author 殇枫
 * @Package com.sf.zhimengjing.exception.handle
 * @description: 全局异常处理器类
 */
 //TODO 全局异常处理，控制器包名
@RestControllerAdvice(basePackages = "com.sf.zhimengjing.controller")
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    // 验证码错误异常
    @ExceptionHandler(CaptchaErrorException.class)
    public Result<String> handleVerifyCodeErrorException(CaptchaErrorException ex) {
        return Result.error(ex.getResultEnum());
    }

    // 账号不存在异常
    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleAccountNotFoundException(AccountNotFoundException ex) {
        return Result.error(ex.getResultEnum());
    }


    // 密码错误异常
    @ExceptionHandler(PasswordErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handlePasswordErrorException(PasswordErrorException ex) {
        log.warn("捕获到密码错误或账户锁定异常: {}", ex.getMessage());
        // 1. 先用枚举创建Result，确保 code 是正确的
        Result<String> result = Result.error(ex.getResultEnum());
        // 2. 用异常中携带的动态消息，覆盖掉默认消息
        result.setMessage(ex.getMessage());
        return result;
    }

    // 账号封禁异常
    @ExceptionHandler(AccountForbiddenException.class)
    public Result<String> handleAccountForbiddenException(AccountForbiddenException ex) {
        log.warn("捕获到账户禁用或锁定异常: {}", ex.getMessage());
        // 同样，先保证 code 正确，再覆盖 message
        Result<String> result = Result.error(ex.getResultEnum());
        result.setMessage(ex.getMessage());
        return result;
    }

    // 登录状态过期异常
    @ExceptionHandler(TokenOverdueException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleTokenOverdueException(TokenOverdueException ex){
        return Result.error(ex.getResultEnum());
    }


    /**
     *
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        return Result.error(ResultEnum.FAIL);
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleValidationExceptions(Exception ex) {
        log.error(ex.getMessage());
        // 从异常中获取字段错误信息
        FieldError fieldError = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError();
        if (fieldError != null) {
            // 获取错误提示信息
            String errorMessage = fieldError.getDefaultMessage();
            log.error(errorMessage);
            return Result.error(errorMessage);
        } else {
            // 如果没有字段错误，返回默认错误信息
            log.error(ex.getMessage());
            return Result.error("请求参数验证失败");
        }
    }
}
