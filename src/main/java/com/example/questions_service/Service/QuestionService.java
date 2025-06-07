package com.example.questions_service.Service;

import com.example.questions_service.Entity.Question;
import com.example.questions_service.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    public void createQuestion(Question question){
        questionRepository.save(question);
    }

    public List<Question> getQuestions(String category, int numberOfEasy, int numberOfMedium, int numberOfDifficult){
        List<Question> questions1;
        questions1 = questionRepository.findByCategoryAndDifficulty(category, "EASY");
        Collections.shuffle(questions1);
        List<Question> questions2;
        questions2 = questionRepository.findByCategoryAndDifficulty(category, "EASY");
        Collections.shuffle(questions2);
        List<Question> questions3;
        questions3 = questionRepository.findByCategoryAndDifficulty(category, "EASY");
        Collections.shuffle(questions3);
        questions2.addAll(questions3);
        questions1.addAll(questions2);
        return questions1;
    }
}
