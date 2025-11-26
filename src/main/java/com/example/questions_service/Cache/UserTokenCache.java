package com.example.questions_service.Cache;

import com.example.questions_service.Service.UserService;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserTokenCache {

    @Autowired
    private MeterRegistry meterRegistry;
    @Value("${refreshTokenTimeHour}")
    private static int refreshTokenTime;
    private static final Logger logger = LoggerFactory.getLogger(UserTokenCache.class);

    private static final com.github.benmanes.caffeine.cache.Cache<String, String> userTokenCache = Caffeine.newBuilder()
            .expireAfterWrite(refreshTokenTime, TimeUnit.HOURS)
            .recordStats()
            .build();

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, userTokenCache, "userTokenCache");
    }

    public void put(String token, String refreshToken) {
        logger.info("Cache: Storing token in cache");
        userTokenCache.put(token, refreshToken);
        logger.info("Cache: Token stored successfully");
    }

    public String get(String token) {
        logger.info("Cache: Retrieving token from cache");
        return userTokenCache.getIfPresent(token);
    }

    public void invalidateKey(String token) {
        logger.info("Cache: Invalidating token in cache");
        userTokenCache.invalidate(token);
        logger.info("Cache: Token invalidated successfully");
    }
}