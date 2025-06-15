package com.example.questions_service.Service;

import com.example.questions_service.Entity.User;
import com.example.questions_service.Repository.UserRepository;
import com.example.questions_service.Utility.JWTUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Objects;
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

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, userCache, "userCache");
    }
    @Autowired
    UserRepository userRepository;

    private final Cache<String, String> userCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.HOURS)
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

    public String login(String email, String password) throws Exception{
        email = email.toLowerCase();
        Optional<User> user = userRepository.findByEmailID(email);
        if(user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())){
            String token = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken();
            userCache.put(token, refreshToken);
            return token;
        } else {
            throw new Exception("Invalid Credentials");
        }
    }

    public String updateToken(String token, String email) {
        String refreshToken = userCache.getIfPresent(token);
        if(refreshToken != null) {
            userCache.invalidate(token);
            String newToken = jwtUtil.generateAccessToken(email);
            userCache.put(newToken, refreshToken);
            return newToken;
        } else {
            return null;
        }
    }

    public void logout(String token) {
        userCache.invalidate(token);
    }
}
