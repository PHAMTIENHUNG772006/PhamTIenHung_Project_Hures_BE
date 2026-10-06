package com.restaurant.erp.common.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.erp.common.annotation.Idempotent;
import com.restaurant.erp.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private static final String PROCESSING_FLAG = "__PROCESSING__";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CachedIdempotentResponse {
        private int statusCode;
        private String bodyJson;
    }

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String idempotencyKey = request.getHeader("Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            idempotencyKey = request.getHeader("X-Idempotency-Key");
        }

        // If no idempotency key provided, proceed normally
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return joinPoint.proceed();
        }

        String redisKey = "idempotency:key:" + idempotencyKey.trim();
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        if (bucket.isExists()) {
            String cachedValue = bucket.get();
            if (PROCESSING_FLAG.equals(cachedValue)) {
                log.warn("Concurrent request with same Idempotency-Key: {}", idempotencyKey);
                throw new BusinessException("Yêu cầu đang được hệ thống xử lý, vui lòng chờ trong giây lát.");
            }

            try {
                CachedIdempotentResponse cachedResponse = objectMapper.readValue(cachedValue, CachedIdempotentResponse.class);
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Type returnType = signature.getMethod().getGenericReturnType();

                if (returnType instanceof ParameterizedType paramType && ResponseEntity.class.isAssignableFrom((Class<?>) paramType.getRawType())) {
                    Type bodyType = paramType.getActualTypeArguments()[0];
                    JavaType javaBodyType = objectMapper.constructType(bodyType);
                    Object body = objectMapper.readValue(cachedResponse.getBodyJson(), javaBodyType);
                    log.info("Idempotent hit for key: [{}]. Returning cached response with status {}", idempotencyKey, cachedResponse.getStatusCode());
                    return ResponseEntity.status(cachedResponse.getStatusCode()).body(body);
                }
            } catch (Exception ex) {
                log.error("Failed to parse cached idempotent response for key {}: {}", idempotencyKey, ex.getMessage());
            }
        }

        // Lock / mark as processing
        bucket.set(PROCESSING_FLAG, 60, TimeUnit.SECONDS);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            bucket.delete(); // Delete lock on error so client can retry
            throw t;
        }

        try {
            if (result instanceof ResponseEntity<?> responseEntity) {
                String bodyJson = objectMapper.writeValueAsString(responseEntity.getBody());
                CachedIdempotentResponse cached = new CachedIdempotentResponse(responseEntity.getStatusCode().value(), bodyJson);
                String serialized = objectMapper.writeValueAsString(cached);
                bucket.set(serialized, idempotent.expireHours(), TimeUnit.HOURS);
                log.info("Idempotent response cached for key: [{}] with TTL {} hours", idempotencyKey, idempotent.expireHours());
            }
        } catch (Exception ex) {
            log.error("Failed to cache idempotent response for key {}: {}", idempotencyKey, ex.getMessage());
        }

        return result;
    }
}
