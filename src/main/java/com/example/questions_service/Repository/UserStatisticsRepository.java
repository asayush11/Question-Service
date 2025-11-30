package com.example.questions_service.Repository;

import com.example.questions_service.DTO.UserStatsResponseDTO;
import com.example.questions_service.Entity.UserStatistics;
import com.example.questions_service.Entity.UserStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, UserStatsId> {
    @Query("SELECT new com.example.questions_service.DTO.UserStatsResponseDTO(" +
            "u.quizId, u.quizType, u.percentageScore, u.dateTaken) " +
            "FROM UserStatistics u WHERE u.emailId = :emailId")
    List<UserStatsResponseDTO> findStatsByEmailId(@Param("emailId") String emailId);
}
