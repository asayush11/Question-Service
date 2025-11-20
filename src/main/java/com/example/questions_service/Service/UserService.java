package com.example.questions_service.Service;

import com.example.questions_service.Entity.User;
import com.example.questions_service.Repository.UserRepository;
import com.example.questions_service.Utility.JWTUtil;
import com.example.questions_service.Utility.LoginValidationResult;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    MeterRegistry meterRegistry;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JWTUtil jwtUtil;
    @Value("${refreshTokenTimeHour}")
    private int refreshTokenTime;

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, userCache, "userCache");
    }
    @Autowired
    UserRepository userRepository;

    private final Cache<String, String> userCache = Caffeine.newBuilder()
            .expireAfterWrite(refreshTokenTime, TimeUnit.HOURS)
            .recordStats()
            .build();

    public boolean createUser(String username, String email, String password) {
        email = email.toLowerCase();
        if(userRepository.findByEmailID(email).isPresent()){
            return false;
        } else {
            User newUser = new User(username, email, passwordEncoder.encode(password));
            userRepository.save(newUser);
            return true;
        }
    }

    public LoginValidationResult login(String email, String password) throws IllegalArgumentException{
        email = email.toLowerCase();
        Optional<User> user = userRepository.findByEmailID(email);
        if(user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())){
            String token = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken();
            userCache.put(token, refreshToken);
            return new LoginValidationResult(token, user.get().getUsername(), user.get().getQuizzesTaken(), user.get().getAdmin());
        } else {
            throw new IllegalArgumentException("Invalid Credentials");
        }
    }

    public String updateToken(String token, String email) {
        String refreshToken = userCache.getIfPresent(token);
        if(refreshToken != null) {
            userCache.invalidate(token);
            String newToken = jwtUtil.generateNewAccessToken(refreshToken, email);
            if(token == null) return null;
            userCache.put(newToken, refreshToken);
            return newToken;
        } else {
            return null;
        }
    }

    public void logout(String token) {
        userCache.invalidate(token);
    }

    public boolean changePassword(String email, String password) throws IllegalArgumentException{
        email = email.toLowerCase();
        Optional<User> user = userRepository.findByEmailID(email);
        if(user.isPresent()){
            User updatedUser = user.get();
            updatedUser.setPassword(passwordEncoder.encode(password));
            userRepository.save(updatedUser);
            return true;
        } else {
            throw new IllegalArgumentException("User doesn't exist");
        }
    }

    public void deleteAccount(String email) {
        userRepository.deleteByEmail(email);
    }
}
