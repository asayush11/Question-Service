package com.example.questions_service.Cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserTokenCache {

    @Autowired
    private MeterRegistry meterRegistry;

    @Value("${refreshTokenTimeHour}")
    private int refreshTokenTime;

    private final com.github.benmanes.caffeine.cache.Cache<String, String> userTokenCache = Caffeine.newBuilder()
            .expireAfterWrite(refreshTokenTime, TimeUnit.HOURS)
            .recordStats()
            .build();

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, userTokenCache, "userTokenCache");
    }

    public void put(String token, String refreshToken) {
        userTokenCache.put(token, refreshToken);
    }

    public String get(String token) {
        return userTokenCache.getIfPresent(token);
    }

    public void invalidateKey(String token) {
        userTokenCache.invalidate(token);
    }
}