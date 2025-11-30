package com.example.questions_service.Utility;

import com.example.questions_service.Cache.UserTokenCache;
import com.example.questions_service.Entity.User;
import com.example.questions_service.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserHelper {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    UserTokenCache userTokenCache;
    private static final Logger logger = LoggerFactory.getLogger(UserHelper.class);

    public boolean validateAdmin(String token) throws Exception{
        try {
            logger.info("Helper: Validating admin user");
            String email = jwtUtil.validateToken(token).email();
            Optional<User> user = userRepository.findByEmailID(email);
            return user.isPresent() && user.get().getAdmin();
        } catch (Exception e) {
            logger.error("Helper: Failed to validate admin user: {}", e.getMessage());
            throw new Exception("Failed to validate user");
        }
    }

    public String getAccessToken(String email) {
        logger.info("Helper: Generating access token for email: {}", email);
        return jwtUtil.generateAccessToken(email);
    }

    public String getRefreshToken() {
        logger.info("Helper: Generating refresh token");
        return jwtUtil.generateRefreshToken();
    }

    public String updateToken(String token, String email) {
        logger.info("Helper: Updating access token for email: {} using refresh token", email);
        String refreshToken = userTokenCache.get(token);
        if(refreshToken != null) {
            userTokenCache.invalidateKey(token);
            String newToken = jwtUtil.generateNewAccessToken(refreshToken, email);
            if(token == null) {
                logger.error("Helper: Failed to generate new access token - refresh token expired or invalid");
                return null;
            }
            logger.info("Helper: Access token updated successfully");
            String newRefreshToken = jwtUtil.generateRefreshToken();
            userTokenCache.put(newToken, newRefreshToken);
            return newToken;
        } else {
            logger.error("Helper: Failed to update access token - invalid access token");
            return null;
        }
    }
}
