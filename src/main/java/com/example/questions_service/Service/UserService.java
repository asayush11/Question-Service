package com.example.questions_service.Service;

import com.example.questions_service.Entity.User;
import com.example.questions_service.Repository.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    MeterRegistry meterRegistry;

    @PostConstruct
    public void bindCaffeineCacheMetrics() {
        CaffeineCacheMetrics.monitor(meterRegistry, userCache, "userCache");
    }
    @Autowired
    UserRepository userRepository;

    private static final int MAX_CACHE_SIZE = 100;

    private final Cache<String, Boolean> userCache = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();

    public boolean createUser(String username, String email, String password) {
        if(userRepository.findByEmailID(email).isPresent()){
            return false;
        } else {
            User newUser = new User(username, email, password);
            userRepository.save(newUser);
            userCache.put(email, true);
            return true;
        }
    }

    public boolean login(String email, String password) {
        Optional<User> user = userRepository.findByEmailID(email);
        boolean validUser = user.filter(value -> Objects.equals(value.getPassword(), password)).isPresent();
        if(validUser){
            userCache.invalidate(email);
            userCache.put(email, true);
        }
        return validUser;
    }

    public boolean checkLoggedIn(String email) {
        return Boolean.TRUE.equals(userCache.getIfPresent(email));
    }
}
