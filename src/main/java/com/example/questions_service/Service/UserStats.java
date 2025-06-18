package com.example.questions_service.Service;

import com.example.questions_service.Repository.UserRepository;
import com.example.questions_service.Utility.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UserStats {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTUtil jwtUtil;
    @Async
    public void updateStats(String authHeader, int quizInc, int questionInc) {
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token).email();
            userRepository.incrementStatsByEmail(email, quizInc, questionInc);
        } catch (Exception e) {
            return;
        }
    }

}
