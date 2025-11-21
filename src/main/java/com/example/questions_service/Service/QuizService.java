package com.example.questions_service.Service;
import com.example.questions_service.Cache.QuestionsCache;
import com.example.questions_service.Cache.QuizAnswersCache;
import com.example.questions_service.DTO.AnswerResponseDTO;
import com.example.questions_service.DTO.QuestionResponseDTO;
import com.example.questions_service.DTO.QuizDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Exception.UserNotAdminException;
import com.example.questions_service.Repository.QuestionRepository;
import com.example.questions_service.Utility.UserHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuizService {

    @Autowired
    UserHelper userHelper;
    @Autowired
    QuestionsCache questionsCache;
    @Autowired
    QuizAnswersCache quizAnswersCache;
    @Autowired
    QuestionRepository questionRepository;

    public QuizDTO getQuestions(String category, int numberOfEasy, int numberOfMedium, int numberOfDifficult, String authHeader) {
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
        userHelper.updateStats(authHeader,1);

        String quizID = category + quizAnswersCache.estimatedSize() + " " + numberOfDifficult + " " + numberOfMedium + " " + numberOfEasy;
        List<AnswerResponseDTO> answers = new ArrayList<>();
        List<QuestionResponseDTO> questions = new ArrayList<>();
        for(Question q : result){
            answers.add(new AnswerResponseDTO(q.getAnswer(), q.getSolution()));
            questions.add(new QuestionResponseDTO(q.getQuestion(), q.getCategory(), q.getDifficulty(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getType()));
        }
        quizAnswersCache.put(quizID, answers);
        return new QuizDTO(questions, quizID);
    }

    private List<Question> getAllQuestions(String topic, String difficulty) {
        List<Question> questions = questionsCache.get(topic, difficulty);
        if(questions != null){
            return questions;
        }
        questions = fetchQuestionsFromDatabase(topic, difficulty);
        questionsCache.put(topic, difficulty, questions);
        return questions;
    }

    private List<Question> fetchQuestionsFromDatabase(String topic, String difficulty) {
        try {
            List<Question> questions = questionRepository.findByCategoryAndDifficulty(topic, difficulty);
            return questions != null ? questions : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<AnswerResponseDTO> getAnswers(String quizId){
        var answers = quizAnswersCache.get(quizId);
        quizAnswersCache.invalidateKey(quizId);
        return answers;
    }
}
