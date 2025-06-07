package com.example.questions_service.Service;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    public void createQuestion(Question question){
        questionRepository.save(question);
    }

    public List<Question> getQuestions(String category, int numberOfEasy, int numberOfMedium, int numberOfDifficult){
        List<Question> result = new ArrayList<>();
        Map<String, Integer> difficultyCount = Map.of(
                "EASY", numberOfEasy,
                "MEDIUM", numberOfMedium,
                "HARD",numberOfDifficult
        );

        for(Map.Entry<String, Integer> entry : difficultyCount.entrySet()) {
            String difficulty = entry.getKey();
            Integer count = entry.getValue();
            List<Question> questions = questionRepository.findByCategoryAndDifficulty(category, difficulty);
            Collections.shuffle(questions);
            result.addAll(questions.subList(0, Math.min(count, questions.size())));
        }
        return result;
    }
}
