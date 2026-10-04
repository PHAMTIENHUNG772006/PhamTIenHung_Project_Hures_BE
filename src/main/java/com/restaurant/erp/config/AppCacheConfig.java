package com.restaurant.erp.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class AppCacheConfig {

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>();
        // Categories cache: TTL 60 minutes, maxIdleTime 30 minutes
        config.put("categories", new CacheConfig(60 * 60 * 1000, 30 * 60 * 1000));
        // MenuItems cache: TTL 60 minutes, maxIdleTime 30 minutes
        config.put("menuItems", new CacheConfig(60 * 60 * 1000, 30 * 60 * 1000));
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
