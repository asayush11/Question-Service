package com.example.questions_service.Repository;

import com.example.questions_service.Entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, String> {
    Optional<List<UserStatistics>> findByEmailID(String emailID);
}
