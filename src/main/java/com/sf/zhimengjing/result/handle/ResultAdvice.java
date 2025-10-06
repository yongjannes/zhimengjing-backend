package com.sf.zhimengjing.result.handle;

import cn.hutool.json.JSONUtil;
import com.sf.zhimengjing.common.result.Result;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import reactor.core.publisher.Flux;

/**
 * @Title: ResultAdvice
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.aop
 * @Description: 统一结果封装增强器 (最终修正版)
 */
//TODO 控制器包名
@RestControllerAdvice(basePackages = "com.sf.zhimengjing.controller") // 指定要增强的控制器包
@NonNullApi
public class ResultAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否支持对返回类型的处理
     * @param returnType 方法返回值类型
     * @param converterType 转换器类型
     * @return 是否进行增强处理
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 不处理响应体为 Flux（流式响应）的情况
        return !Flux.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * 在响应体写入之前进行处理
     * @param body 响应体对象
     * @param returnType 方法返回值类型
     * @param selectedContentType 响应内容类型
     * @param selectedConverterType 转换器类型
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 处理后的响应体
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 1. 响应体为 null 时处理
        //    - 对于 OPTIONS 预检请求和返回 void 的方法很重要
        //    - 对于 String 类型，返回 JSON 格式的 Result.success(null)
        if (body == null) {
            if (String.class.equals(returnType.getGenericParameterType())) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return JSONUtil.toJsonStr(Result.success(null));
            }
            return Result.success();
        }

        // 2. 如果响应体已经是 Result 类型，直接返回，避免重复包装
        if (body instanceof Result) {
            return body;
        }

        // 3. 流式响应（Server-Sent Events）直接返回，不进行封装
        if (MediaType.TEXT_EVENT_STREAM.isCompatibleWith(selectedContentType)) {
            return body;
        }

        // 4. 响应体为 String 类型时特殊处理
        //    - Spring 对 String 类型会直接写入响应，而不是通过 Jackson 转 JSON
        //    - 需要手动将封装后的 Result 转成 JSON 字符串
        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JSONUtil.toJsonStr(Result.success(body));
        }

        // 5. 其他类型的响应体，统一包装成 Result.success(body)
        return Result.success(body);
    }
}
