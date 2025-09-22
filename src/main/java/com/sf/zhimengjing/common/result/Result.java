package com.sf.zhimengjing.common.result;

import com.sf.zhimengjing.common.enumerate.ResultEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: Result
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.result
 * @description: 统一格式封装
 */
@Data
@Schema(description = "统一响应对象")
public class Result<T> {

    @Schema(description = "操作代码")
    private Integer code;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "结果数据")
    private T data;

    public Result(ResultEnum resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public Result(ResultEnum resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public Result(String message) {
        this.code = ResultEnum.FAIL.getCode();
        this.message = message;
    }

    // 成功返回封装-无数据
    public static Result<String> success() {
        return new Result<>(ResultEnum.SUCCESS);
    }

    // 成功返回封装-带数据
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultEnum.SUCCESS, data);
    }

    // 失败返回封装-使用默认提示信息
    public static Result<String> error() {
        return new Result<>(ResultEnum.FAIL);
    }

    // 失败返回封装-使用返回结果枚举提示信息
    public static Result<String> error(ResultEnum resultCode) {
        return new Result<>(resultCode);
    }

    // 失败返回封装-使用自定义提示信息
    public static Result<String> error(String message) {
        return new Result<>(message);
    }
}
