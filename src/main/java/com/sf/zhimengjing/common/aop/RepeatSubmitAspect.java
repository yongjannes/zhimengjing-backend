package com.sf.zhimengjing.common.aop;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.sf.zhimengjing.common.annotation.RepeatSubmit;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @Title: RepeatSubmitAspect
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.aop
 * @description: 防止重复提交
 */
@Aspect
@Component
@Slf4j
public class RepeatSubmitAspect {
    private final StringRedisTemplate redisTemplate;

    public RepeatSubmitAspect(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Before("@annotation(repeatSubmit)")
    public void before(JoinPoint joinPoint, RepeatSubmit repeatSubmit) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;

        if (attributes != null) {
            request = attributes.getRequest();
        }

        //请求参数拼接
        String requestParams = argsArrayToString(joinPoint.getArgs());

        String authorizationHeader = null;
        if (request != null) {
            authorizationHeader = request.getHeader("Authorization");
        }

        String submitKey = null;

        if (authorizationHeader != null) {
            //如果存在token则通过token+请求参数生成唯一标识
            String token = StringUtils.removeStart(authorizationHeader, "Bearer ");
            submitKey= SecureUtil.md5(token+":"+requestParams);

        } else{
            //不存在token则通过请求url+参数生成唯一标识
            if (request != null) {
                submitKey = SecureUtil.md5(request.getRequestURL().toString()+":"+requestParams);
            }
        }
        //缓存key
        String cacheKey = "repeat_submit:"+submitKey;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            throw new GeneralBusinessException(repeatSubmit.message());
        }
        redisTemplate.opsForValue().set(cacheKey, "1", repeatSubmit.interval(), repeatSubmit.timeUnit());
    }


    /**
     * 参数拼接
     * @param args  参数数组
     * @return 拼接后的字符串
     */
    private String argsArrayToString(Object[] args){
        StringBuilder params = new StringBuilder();
        if(args!= null && args.length > 0){
            for(Object o:args){
                if(Objects.nonNull(o)&&!isFilterObject(o)){
                    try {
                        params.append(JSONUtil.toJsonStr(o)).append(" ");
                    }catch (Exception e){
                        log.error("参数拼接异常:{}",e.getMessage());
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否需要过滤的对象。
     * @param o  对象
     * @return true：需要过滤；false：不需要过滤
     */
    private boolean isFilterObject(final Object o) {
        Class<?> c = o.getClass();
        //如果是数组且类型为文件类型的需要过滤
        if(c.isArray()){
            return  c.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        //如果是集合且类型为文件类型的需要过滤
        else if(Collection.class.isAssignableFrom(c)){
            Collection collection = (Collection) o;
            for(Object value:collection){
                return value instanceof MultipartFile;
            }
        }
        //如果是Map且类型为文件类型的需要过滤
        else if(Map.class.isAssignableFrom(c)){
            Map map = (Map) o;
            for(Object value:map.entrySet()){
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        //如果是文件类型的需要过滤
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
