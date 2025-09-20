package com.sf.zhimengjing.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.util.IpUtils;
import com.sf.zhimengjing.entity.admin.AdminOperationLog;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminOperationLogMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
 *               请求参数、响应结果以及方法执行时间。
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
     * 环绕通知：记录方法执行时间，并插入日志
     *
     * @param joinPoint 连接点
     * @return 方法返回结果
     * @throws Throwable 异常
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long executeTime = System.currentTimeMillis() - startTime;
            handleLog(joinPoint, result, executeTime);
        }
    }

    /**
     * 处理日志记录逻辑
     *
     * @param joinPoint   连接点
     * @param jsonResult  方法返回结果
     * @param executeTime 方法执行时间(毫秒)
     */
    protected void handleLog(final ProceedingJoinPoint joinPoint, Object jsonResult, long executeTime) {
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

            // 使用ObjectMapper将对象序列化为JSON字符串
            operLog.setRequestParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
            operLog.setResponseResult(objectMapper.writeValueAsString(jsonResult));

            // 设置方法执行时间
            operLog.setExecuteTime((int) executeTime);

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
    private Log getAnnotationLog(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            return signature.getMethod().getAnnotation(Log.class);
        } catch (Exception e) {
            return null;
        }
    }
}
