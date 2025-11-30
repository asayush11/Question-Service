package com.example.questions_service.Controller;

import com.example.questions_service.DTO.QuestionRequestDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Exception.UserNotAdminException;
import com.example.questions_service.Service.QuestionService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    QuestionService questionService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @PostMapping("/create")
    public ResponseEntity<APIResponse<String>> addQuestion(@Valid @RequestBody QuestionRequestDTO question){
        logger.info("Controller: Attempting to add new question: {}", question.getQuestion());
        if(!Objects.equals(question.getDifficulty(), "HARD") || !Objects.equals(question.getDifficulty(), "EASY") || !Objects.equals(question.getDifficulty(), "MEDIUM")){
            logger.error("Controller: Failed to add question - invalid difficulty: {}", question.getDifficulty());
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Failed to add question","Enter Valid Difficulty"));
        }
        try{
            Question newQuestion = new Question(question);
            questionService.createQuestion(newQuestion);
            logger.info("Controller: Question added successfully: {}", question.getQuestion());
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Question added successfully", newQuestion.getQuestion()));
        } catch (Exception e){
            logger.error("Controller: Server Error in trying to add question: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to add question",e.getMessage()));
        }
    }
}
