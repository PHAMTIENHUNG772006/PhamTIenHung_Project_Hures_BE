package com.restaurant.erp.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {

    @AfterReturning(pointcut = "@annotation(com.restaurant.erp.common.audit.Auditable)", returning = "result")
    public void logAudit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "SYSTEM";

        String action = auditable.action();
        String methodName = method.getName();
        Object[] args = joinPoint.getArgs();

        log.info("[AUDIT LOG] User: {}, Action: {}, Method: {}, Arguments: {}, Result: {}",
                username, action, methodName, args, result);
    }
}
