package com.example.questions_service.Cache;

import com.example.questions_service.Entity.Question;
import com.github.benmanes.caffeine.cache.Cache;
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
public class QuizCache {
    @Autowired
    private MeterRegistry meterRegistry;
    @Value("${liveQuizDurationHours}")
    private int liveQuizDurationHours;
    private static final Logger logger = LoggerFactory.getLogger(QuizCache.class);
    private Cache<String, List<Question>> mockQuiz;
    private Cache<String, List<Question>> liveQuiz;

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        mockQuiz = Caffeine.newBuilder()
                .recordStats()
                .build();

        liveQuiz = Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(liveQuizDurationHours, TimeUnit.HOURS)
                .recordStats()
                .build();

        CaffeineCacheMetrics.monitor(meterRegistry, mockQuiz, "mockQuizCache");
        CaffeineCacheMetrics.monitor(meterRegistry, liveQuiz, "liveQuizCache");
    }

    public List<Question> getMockQuiz(String key) {
        logger.info("Cache: Retrieving mock quiz from cache for key: {}", key);
        return mockQuiz.getIfPresent(key);
    }

    public void putMockQuiz(String key, List<Question> questions) {
        logger.info("Cache: Storing mock quiz in cache for key: {}", key);
        mockQuiz.put(key, questions);
    }

    public List<Question> getLiveQuiz() {
        logger.info("Cache: Retrieving live quiz from cache");
        return liveQuiz.getIfPresent("Live");
    }

    public void putLiveQuiz(List<Question> questions) {
        logger.info("Cache: Storing live quiz in cache");
        liveQuiz.put("Live", questions);
    }

    public void clearMockQuizCache() {
        logger.info("Cache: Clearing mock quiz cache");
        mockQuiz.invalidateAll();
    }
}
