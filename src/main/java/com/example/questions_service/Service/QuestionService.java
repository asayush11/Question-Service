package com.example.questions_service.Service;

import com.example.questions_service.Cache.QuestionsCache;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Exception.UserNotAdminException;
import com.example.questions_service.Repository.QuestionRepository;
import com.example.questions_service.Utility.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    UserHelper userHelper;
    @Autowired
    QuestionsCache questionsCache;
    public void createQuestion(Question question, String authHeader) throws Exception{
        try {
            questionRepository.save(question);
            questionsCache.invalidateKey(question.getCategory(), question.getDifficulty());
        } catch (Exception e) {
            throw new Exception("Failed to add question", e);
        }
    }
}
