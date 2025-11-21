package com.example.questions_service.Utility;

import com.example.questions_service.Cache.UserTokenCache;
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
    @Autowired
    UserTokenCache userTokenCache;
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

    public boolean validateAdmin(String token) throws Exception{
        try {
            String email = jwtUtil.validateToken(token).email();
            Optional<User> user = userRepository.findByEmailID(email);
            return user.isPresent() && user.get().getAdmin();
        } catch (Exception e) {
            throw new Exception("Failed to validate user");
        }
    }

    public String getAccessToken(String email) {
        return jwtUtil.generateAccessToken(email);
    }

    public String getRefreshToken() {
        return jwtUtil.generateRefreshToken();
    }

    public String updateToken(String token, String email) {
        String refreshToken = userTokenCache.get(token);
        if(refreshToken != null) {
            userTokenCache.invalidateKey(token);
            String newToken = jwtUtil.generateNewAccessToken(refreshToken, email);
            if(token == null) return null;
            userTokenCache.put(newToken, refreshToken);
            return newToken;
        } else {
            return null;
        }
    }
}
