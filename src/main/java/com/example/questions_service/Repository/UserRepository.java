package com.example.questions_service.Repository;

import com.example.questions_service.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailID(String emailID);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.quizzesTaken = u.quizzesTaken + :quizInc, " +
            "u.questionsContributed = u.questionsContributed + :questionInc " +
            "WHERE u.emailID = :email")
    int incrementStatsByEmail(@Param("email") String email,
                              @Param("quizInc") int quizInc,
                              @Param("questionInc") int questionInc);
}
