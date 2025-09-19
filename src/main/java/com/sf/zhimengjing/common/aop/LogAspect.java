package com.sf.zhimengjing.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.util.IpUtils;
import com.sf.zhimengjing.entity.AdminOperationLog;
import com.sf.zhimengjing.entity.AdminUser;
import com.sf.zhimengjing.mapper.AdminOperationLogMapper;
import com.sf.zhimengjing.mapper.AdminUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Title: LogAspect
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.aop
 * @description: 系统操作日志切面
 *               使用 AOP 拦截标注了 @Log 注解的方法，实现操作日志的统一记录。
 *               日志包括用户信息、请求路径、请求方法、IP 地址、模块、操作内容、
 *               请求参数以及响应结果。
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final AdminOperationLogMapper adminOperationLogMapper;
    private final AdminUserMapper adminUserMapper;
    private final ObjectMapper objectMapper;


    /**
     * 定义切点：拦截所有标注 @Log 注解的方法
     */
    @Pointcut("@annotation(com.sf.zhimengjing.common.annotation.Log)")
    public void logPointCut() {}

    /**
     * 后置通知：方法返回后执行，记录操作日志
     *
     * @param joinPoint 连接点
     * @param jsonResult 方法返回结果
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, jsonResult);
    }


    /**
     * 处理日志记录逻辑
     *
     * @param joinPoint 连接点
     * @param jsonResult 方法返回结果
     */
    protected void handleLog(final JoinPoint joinPoint, Object jsonResult) {
        try {
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) return;

            AdminUser loginUser = getLoginUser();
            AdminOperationLog operLog = new AdminOperationLog();
            if(loginUser != null){
                operLog.setAdminId(loginUser.getId());
                operLog.setAdminName(loginUser.getRealName());
            }

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            operLog.setRequestPath(request.getRequestURI());
            operLog.setIpAddress(IpUtils.getIpAddr(request));
            operLog.setRequestMethod(request.getMethod());
            operLog.setModule(controllerLog.module());
            operLog.setOperation(controllerLog.operation());

            // 关键：使用ObjectMapper将对象序列化为JSON字符串
            operLog.setRequestParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
            operLog.setResponseResult(objectMapper.writeValueAsString(jsonResult));

            adminOperationLogMapper.insert(operLog);
        } catch (Exception ignored) {
            // 日志记录的任何异常都不应影响主业务流程
        }
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户对象，若无法获取返回 null
     */
    private AdminUser getLoginUser() {
        try {
            String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
            return adminUserMapper.selectById(adminId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取方法上的 @Log 注解
     *
     * @param joinPoint 连接点
     * @return Log 注解对象，若方法未标注返回 null
     */
    private Log getAnnotationLog(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            return signature.getMethod().getAnnotation(Log.class);
        } catch (Exception e) {
            return null;
        }
    }
}