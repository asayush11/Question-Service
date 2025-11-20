package com.example.questions_service.Utility;

import com.example.questions_service.Entity.User;
import com.example.questions_service.Repository.UserRepository;
import com.example.questions_service.Utility.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Component
public class UserHelper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTUtil jwtUtil;
    @Async
    public void updateStats(String authHeader, int quizInc) {
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token).email();
            userRepository.incrementStatsByEmail(email, quizInc);
        } catch (Exception e) {
            return;
        }
    }

    public boolean validateAdmin(String authHeader) throws Exception{
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token).email();
            Optional<User> user = userRepository.findByEmailID(email);
            return user.isPresent() && user.get().getAdmin();
        } catch (Exception e) {
            throw new Exception("Failed to validate user");
        }
    }

}
