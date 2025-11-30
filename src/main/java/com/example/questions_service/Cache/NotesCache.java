package com.example.questions_service.Cache;

import com.example.questions_service.Entity.Question;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class NotesCache {
    @Autowired
    private MeterRegistry meterRegistry;
    @Value("${notesCacheSize}")
    private int notesCacheSize;
    private static final Logger logger = LoggerFactory.getLogger(NotesCache.class);

    private final com.github.benmanes.caffeine.cache.Cache<String, String> notesCache = Caffeine.newBuilder()
            .maximumSize(notesCacheSize)
            .recordStats()
            .build();

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, notesCache, "notesCache");
    }

    public String get(String subject, String topic) {
        logger.info("Cache: Retrieving notes from cache for subject: {} and topic: {}", subject, topic);
        String key = getCacheKey(subject, topic);
        return notesCache.getIfPresent(key);
    }

    private String getCacheKey(String subject, String topic) {
        return subject.toUpperCase().trim() + "_" + topic.toUpperCase().trim();
    }

    public void put(String subject, String topic, String content) {
        logger.info("Cache: Storing notes in cache for subject: {} and topic: {}", subject, topic);
        String key = getCacheKey(subject, topic);
        notesCache.put(key, content);
    }

    public void remove(String subject, String topic) {
        logger.info("Cache: Removing notes from cache for subject: {} and topic: {}", subject, topic);
        String key = getCacheKey(subject, topic);
        notesCache.invalidate(key);
    }
}
