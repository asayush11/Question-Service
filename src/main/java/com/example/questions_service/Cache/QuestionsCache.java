package com.example.questions_service.Cache;

import com.example.questions_service.Controller.UserController;
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
public class QuestionsCache {

    @Autowired
    private MeterRegistry meterRegistry;
    @Value("${questionCacheSize}")
    private int questionCacheSize;
    @Value("${questionCacheHour}")
    private int questionCacheHour;
    private static final Logger logger = LoggerFactory.getLogger(QuestionsCache.class);

    private final com.github.benmanes.caffeine.cache.Cache<String, List<Question>> questionsCache = Caffeine.newBuilder()
            .maximumSize(questionCacheSize)
            .expireAfterAccess(questionCacheHour, TimeUnit.HOURS)
            .recordStats()
            .build();

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, questionsCache, "questionsCache");
    }

    public List<Question> get(String topic, String difficulty) {
        logger.info("Cache: Retrieving questions from cache for topic: {} and difficulty: {}", topic, difficulty);
        String key = getCacheKey(topic, difficulty);
        return questionsCache.getIfPresent(key);
    }

    public void put(String topic, String difficulty, List<Question> questions) {
        logger.info("Cache: Storing questions in cache for topic: {} and difficulty: {}", topic, difficulty);
        String key = getCacheKey(topic, difficulty);
        questionsCache.put(key, questions);
    }

    public void invalidateKey(String category, String difficulty) {
        logger.info("Cache: Invalidating cache for category: {} and difficulty: {}", category, difficulty);
        String key = getCacheKey(category, difficulty);
        questionsCache.invalidate(key);
    }
    private String getCacheKey(String topic, String difficulty) {
        return topic.toUpperCase().trim() + "_" + difficulty.toUpperCase().trim();
    }
}