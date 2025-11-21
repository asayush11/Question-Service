package com.example.questions_service.Cache;

import com.example.questions_service.DTO.AnswerResponseDTO;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
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

    private final com.github.benmanes.caffeine.cache.Cache<String, List<AnswerResponseDTO>> quizAnswersCache = Caffeine.newBuilder()
            .expireAfterAccess(answersTimeHour, TimeUnit.HOURS)
            .recordStats()
            .build();

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, quizAnswersCache, "quizAnswersCache");
    }

    public List<AnswerResponseDTO> get(String quizId) {
        return quizAnswersCache.getIfPresent(quizId);
    }

    public void put(String quizId, List<AnswerResponseDTO> answers) {
        quizAnswersCache.put(quizId, answers);
    }

    public void invalidateKey(String quizId) {
        quizAnswersCache.invalidate(quizId);
    }
    public long estimatedSize() {
        return quizAnswersCache.estimatedSize();
    }
}