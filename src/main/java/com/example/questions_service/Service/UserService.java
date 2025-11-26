package com.example.questions_service.Service;

import com.example.questions_service.Cache.UserTokenCache;
import com.example.questions_service.Controller.UserController;
import com.example.questions_service.Entity.User;
import com.example.questions_service.Exception.InvalidUserException;
import com.example.questions_service.Repository.UserRepository;
import com.example.questions_service.Utility.JWTUtil;
import com.example.questions_service.Utility.LoginValidationResult;
import com.example.questions_service.Utility.UserHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserHelper userHelper;
    @Autowired
    UserTokenCache userTokenCache;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public boolean createUser(String username, String email, String password) {
        logger.info("Service: Creating user with email: {}", email);
        email = email.toLowerCase();
        if(userRepository.findByEmailID(email).isPresent()){
            logger.error("Service: User creation failed - account with email already exists: {}", email);
            return false;
        } else {
            User newUser = new User(username, email, passwordEncoder.encode(password));
            userRepository.save(newUser);
            logger.info("Service: User created successfully with email: {}", email);
            return true;
        }
    }

    public LoginValidationResult login(String email, String password) throws InvalidUserException{
        logger.info("Service: Attempting to log in user with email: {}", email);
        email = email.toLowerCase();
        Optional<User> user = userRepository.findByEmailID(email);
        if(user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())){
            String token = userHelper.getAccessToken(email);
            String refreshToken = userHelper.getRefreshToken();
            logger.info("Service: Login successful for user with email: {}", email);
            userTokenCache.put(token, refreshToken);
            return new LoginValidationResult(token, user.get().getUsername(), user.get().getQuizzesTaken(), user.get().getAdmin());
        } else {
            logger.error("Service: Login failed for user with email: {}: Invalid Credentials", email);
            throw new InvalidUserException("Invalid Credentials");
        }
    }

    public void logout(String token) {
        logger.info("Service: Logging out user with token: {}", token);
        userTokenCache.invalidateKey(token);
        logger.info("Service: Logout successful for token: {}", token);
    }

    public boolean changePassword(String email, String password) throws InvalidUserException{
        logger.warn("Service: Attempting to change password for user with email: {}", email);
        email = email.toLowerCase();
        Optional<User> user = userRepository.findByEmailID(email);
        if(user.isPresent()){
            User updatedUser = user.get();
            updatedUser.setPassword(passwordEncoder.encode(password));
            userRepository.save(updatedUser);
            logger.info("Service: Password changed successfully for user with email: {}", email);
            return true;
        } else {
            logger.error("Service: Password change failed for user with email: {}: User doesn't exist", email);
            throw new InvalidUserException("User doesn't exist");
        }
    }

    public void deleteAccount(String email) {
        logger.warn("Service: Deleting account for user with email: {}", email);
        userRepository.deleteByEmail(email);
        logger.info("Service: Account deleted successfully for user with email: {}", email);
    }
}
