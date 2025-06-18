package com.example.questions_service.Service;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Repository.QuestionRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionService {

    @Autowired
    MeterRegistry meterRegistry;
    @Autowired
    UserStats userStats;

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, questionCache, "questionCache");
    }
    @Autowired
    QuestionRepository questionRepository;

    private static final int MAX_CACHE_SIZE = 15;

    private final Cache<String, List<Question>> questionCache = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(24, TimeUnit.HOURS)
            .recordStats()
            .build();

    public List<Question> getQuestions(String category, int numberOfEasy, int numberOfMedium, int numberOfDifficult, String authHeader) {
        List<Question> result = new ArrayList<>();
        Map<String, Integer> difficultyCount = Map.of(
                "EASY", numberOfEasy,
                "MEDIUM", numberOfMedium,
                "HARD", numberOfDifficult
        );

        for (Map.Entry<String, Integer> entry : difficultyCount.entrySet()) {
            String difficulty = entry.getKey();
            int count = entry.getValue();
            List<Question> questions = getAllQuestions(category, difficulty);
            Collections.shuffle(questions);
            result.addAll(questions.subList(0, Math.min(count, questions.size())));
        }
        userStats.updateStats(authHeader,1,0);
        return result;
    }

    private List<Question> getAllQuestions(String topic, String difficulty) {
        String key = getCacheKey(topic, difficulty);
        return questionCache.get(key, k -> fetchQuestionsFromDatabase(topic, difficulty));
    }

    private List<Question> fetchQuestionsFromDatabase(String topic, String difficulty) {
        try {
            List<Question> questions = questionRepository.findByCategoryAndDifficulty(topic, difficulty);
            return questions != null ? questions : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getCacheKey(String topic, String difficulty) {
        return topic.toUpperCase().trim() + "_" + difficulty.toUpperCase().trim();
    }

    public void createQuestion(Question question, String authHeader) {
        try {
            questionRepository.save(question);
            invalidateCache(question.getCategory(), question.getDifficulty());
            userStats.updateStats(authHeader,0,1);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add question", e);
        }
    }

    private void invalidateCache(String topic, String difficulty) {
        String key = getCacheKey(topic, difficulty);
        questionCache.invalidate(key);
    }
}
