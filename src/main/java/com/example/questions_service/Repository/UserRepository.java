package com.example.questions_service.Repository;

import com.example.questions_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailID(String emailID);
}
