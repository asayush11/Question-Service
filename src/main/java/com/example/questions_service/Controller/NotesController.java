package com.example.questions_service.Controller;

import com.example.questions_service.Entity.Notes;
import com.example.questions_service.Service.NotesService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/notes")
public class NotesController {

    @Autowired
    NotesService notesService;
    private static final Logger logger = LoggerFactory.getLogger(NotesController.class);

    @GetMapping("/getTopics/{subject}")
    public ResponseEntity<APIResponse<List<String>>> getTopics(@PathVariable String subject) {
        try{
            logger.info("Controller: Fetching topics for subject: {}", subject);
            return ResponseEntity.ok().body(APIResponse.success("Topics Fetched", notesService.getTopicsBySubject(subject)));
        } catch (Exception e){
            logger.error("Controller: Server Error fetching topics for subject {}: {}", subject, e.getMessage());
            return ResponseEntity.internalServerError().body(APIResponse.error("Error fetching topics", e.getMessage()));
        }
    }

    @GetMapping("/getContent/{subject}/{topic}")
    public ResponseEntity<APIResponse<String>> getContent(@PathVariable String subject, @PathVariable String topic) {
        try{
            logger.info("Controller: Fetching content for subject: {} and topic: {}", subject, topic);
            return ResponseEntity.ok().body(APIResponse.success("Content Fetched", notesService.getContentBySubjectAndTopic(subject, topic)));
        } catch (Exception e){
            logger.error("Controller: Server Error fetching content for subject {} and topic {}: {}", subject, topic, e.getMessage());
            return ResponseEntity.internalServerError().body(APIResponse.error("Error fetching content", e.getMessage()));
        }
    }

    @PostMapping("/addNote")
    public ResponseEntity<APIResponse<String>> addNote(@Valid @RequestBody Notes note) {
        try{
            logger.info("Controller: Adding note for subject: {}, topic: {}", note.getSubject(), note.getTopic());
            notesService.addNote(note.getSubject(), note.getTopic(), note.getContent());
            return ResponseEntity.ok().body(APIResponse.success("Note Added", "Note added successfully"));
        } catch (Exception e){
            logger.error("Controller: Server Error adding note for subject {} and topic {}: {}", note.getSubject(), note.getTopic(), e.getMessage());
            return ResponseEntity.internalServerError().body(APIResponse.error("Error adding note", e.getMessage()));
        }
    }

}
