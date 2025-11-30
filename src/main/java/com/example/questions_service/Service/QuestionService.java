package com.example.questions_service.Service;

import com.example.questions_service.Cache.QuestionsCache;
import com.example.questions_service.Controller.UserController;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Exception.UserNotAdminException;
import com.example.questions_service.Repository.QuestionRepository;
import com.example.questions_service.Utility.UserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);
    public void createQuestion(Question question) throws Exception{
        try {
            logger.info("Service: Creating new question: {}", question.getQuestion());
            questionRepository.save(question);
            questionsCache.invalidateKey(question.getSubject(), question.getDifficulty());
        } catch (Exception e) {
            logger.error("Service: Failed to add question: {}", e.getMessage());
            throw new Exception("Failed to add question", e);
        }
    }
}
