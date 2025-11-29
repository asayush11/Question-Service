package com.example.questions_service.Repository;

import com.example.questions_service.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Integer> {
    @Query("SELECT q FROM Question q WHERE q.subject = :subject AND q.difficulty = :difficulty")
    List<Question> findBySubjectAndDifficulty(@Param("subject") String subject, @Param("difficulty") String difficulty);

    @Query(value = "SELECT 1", nativeQuery = true)
    void wakeDbLightly();
}
