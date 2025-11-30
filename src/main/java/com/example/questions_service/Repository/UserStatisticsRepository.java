package com.example.questions_service.Repository;

import com.example.questions_service.DTO.UserStatsResponseDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Entity.UserStatistics;
import com.example.questions_service.Entity.UserStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, UserStatsId> {
    @Query("SELECT u.quiz_id, u.quiz_type, u.percentage_score, u.date_taken FROM user_statistics u WHERE u.email_id = :emailId")
    List<UserStatsResponseDTO> findStatsByEmailId(@Param("emailId") String emailId);
}
