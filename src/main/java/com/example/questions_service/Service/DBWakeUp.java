package com.example.questions_service.Service;

import com.example.questions_service.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DBWakeUp {
    @Autowired
    QuestionRepository questionRepository;
    private final AtomicLong lastWarmUpTime = new AtomicLong(0);

    @Async
    public void warmUpDb() {
        long now = System.currentTimeMillis();
        long lastTime = lastWarmUpTime.get();
        if (now - lastTime < 300_000) return;
        if (lastWarmUpTime.compareAndSet(lastTime, now)) {
            questionRepository.wakeDbLightly();
        }
    }
}
