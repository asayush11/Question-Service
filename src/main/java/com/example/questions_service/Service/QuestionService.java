package com.example.questions_service.Service;

import com.example.questions_service.Entity.Question;
import com.example.questions_service.Repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    private static final Map<String, List<Question>> cache = new ConcurrentHashMap<>();
    private static final Map<String, Integer> frequency = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 15;

    private List<Question> getAllQuestions(String topic, String difficulty) {

        String key = getCacheKey(topic, difficulty);
        frequency.merge(key, 1, Integer::sum);
        List<Question> cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        if (cache.size() >= MAX_CACHE_SIZE) {
            evictLeastFrequentlyUsed();
        }
        List<Question> questions = fetchQuestionsFromDatabase(topic, difficulty);
        cache.put(key, questions);
        return questions;
    }

    private void evictLeastFrequentlyUsed() {
        if (frequency.isEmpty()) {
            return;
        }

        String lfuKey = frequency.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (lfuKey != null) {
            cache.remove(lfuKey);
            frequency.remove(lfuKey);
        }
    }

    private List<Question> fetchQuestionsFromDatabase(String topic, String difficulty) {
        try {
            List<Question> questions = questionRepository.findByCategoryAndDifficulty(topic, difficulty);
            return questions != null ? questions : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void invalidateCache(String topic, String difficulty) {
        String key = getCacheKey(topic, difficulty);
        if (cache.remove(key) != null) {
            frequency.remove(key);
        }
    }

    private String getCacheKey(String topic, String difficulty) {
        return topic.toUpperCase().trim() + "_" + difficulty.toUpperCase().trim();
    }

    @Transactional
    public void createQuestion(Question question) {
        try {
            questionRepository.save(question);
            invalidateCache(question.getSolution(), question.getDifficulty());
        } catch (Exception e) {
            throw new RuntimeException("Failed to add question", e);
        }
    }

    public List<Question> getQuestions(String category, int numberOfEasy, int numberOfMedium, int numberOfDifficult) {
        List<Question> result = new ArrayList<>();
        Map<String, Integer> difficultyCount = Map.of(
                "EASY", numberOfEasy,
                "MEDIUM", numberOfMedium,
                "HARD", numberOfDifficult
        );

        for (Map.Entry<String, Integer> entry : difficultyCount.entrySet()) {
            String difficulty = entry.getKey();
            Integer count = entry.getValue();
            List<Question> questions = getAllQuestions(category, difficulty);
            Collections.shuffle(questions);
            result.addAll(questions.subList(0, Math.min(count, questions.size())));
        }
        return result;
    }
}
