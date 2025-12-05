package com.example.questions_service.Cache;

import com.example.questions_service.DTO.AnswerResponseDTO;
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
public class QuizAnswersCache {

    @Autowired
    private MeterRegistry meterRegistry;
    @Value("${answersTimeHour}")
    private int answersTimeHour;
    private static final Logger logger = LoggerFactory.getLogger(QuizAnswersCache.class);

    private final com.github.benmanes.caffeine.cache.Cache<String, AnswerResponseDTO> quizAnswersCache = Caffeine.newBuilder()
            .expireAfterWrite(answersTimeHour, TimeUnit.HOURS)
            .recordStats()
            .build();

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, quizAnswersCache, "quizAnswersCache");
    }

    public AnswerResponseDTO get(String quizId) {
        logger.info("Cache: Retrieving answers from cache for quizId: {}", quizId);
        return quizAnswersCache.getIfPresent(quizId);
    }

    public void put(String quizId, AnswerResponseDTO answers) {
        logger.info("Cache: Storing answers in cache for quizId: {}", quizId);
        quizAnswersCache.put(quizId, answers);
    }

    public void invalidateKey(String quizId) {
        logger.info("Cache: Invalidating cache for quizId: {}", quizId);
        quizAnswersCache.invalidate(quizId);
    }
    public long estimatedSize() {
        return quizAnswersCache.estimatedSize();
    }
}