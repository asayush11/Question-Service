package com.example.questions_service.Controller;

import com.example.questions_service.DTO.QuestionDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Service.QuestionService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<String>> addQuestion(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody QuestionDTO question){
       if(!Objects.equals(question.getDifficulty(), "HARD") || !Objects.equals(question.getDifficulty(), "EASY") || !Objects.equals(question.getDifficulty(), "MEDIUM")){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Failed to add question","Enter Valid Difficulty"));
        }
        try{
            Question newQuestion = new Question(question);
            questionService.createQuestion(newQuestion, authHeader);
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Question added successfully", newQuestion.getQuestion()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to add question",e.getMessage()));
        }
    }

    @GetMapping("/retrieve")
    public ResponseEntity<APIResponse<List<Question>>> getQuestions(@RequestHeader("Authorization") String authHeader, @RequestParam String category, @RequestParam int numberOfEasy, @RequestParam int numberOfMedium, @RequestParam int numberOfDifficult){
        if(numberOfDifficult < 0 || numberOfEasy < 0 || numberOfMedium < 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Invalid number of question", "Invalid number of question"));
        }
        try{
            return ResponseEntity.ok().body(APIResponse.success("Questions Fetched",questionService.getQuestions(category, numberOfEasy, numberOfMedium, numberOfDifficult, authHeader)));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching question", e.getMessage()));
        }
    }
}
