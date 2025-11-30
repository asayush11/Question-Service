package com.example.questions_service.Repository;

import com.example.questions_service.Entity.Notes;
import com.example.questions_service.Entity.NotesId;
import com.example.questions_service.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes, NotesId> {

    @Query("SELECT n.topic FROM Notes n WHERE n.subject = :subject")
    List<String> findBySubject(@Param("subject") String subject);
}
