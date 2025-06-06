package com.example.questions_service.Service;

import com.example.questions_service.Entity.Question;
import com.example.questions_service.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    public void createQuestion(Question question){
        questionRepository.save(question);
    }

    public List<Question> getQuestions(String category, String difficulty, int numberOfQuestion){
        List<Question> questions = questionRepository.findByCategoryAndDifficulty(category, difficulty);
        Collections.shuffle(questions);
        return questions.subList(0, Math.min(numberOfQuestion, questions.size()));
    }
}
