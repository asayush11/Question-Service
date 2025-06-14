package com.example.questions_service.Service;

import com.example.questions_service.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DBWakeUp {
    @Autowired
    QuestionRepository questionRepository;

    @Scheduled(fixedDelay = 270000)
    public void warmUpDb() {
        questionRepository.wakeDbLightly();
    }
}
