package com.example.questions_service.Controller;

import com.example.questions_service.DTO.QuestionDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Service.QuestionService;
import com.example.questions_service.Service.UserService;
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

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<String>> addQuestion(@Valid @RequestBody QuestionDTO question, @CookieValue(value = "email", required = false) String email){
        if (email == null || !userService.checkLoggedIn(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Invalid user","Invalid User"));
        }
        if(!Objects.equals(question.getDifficulty(), "HARD") || !Objects.equals(question.getDifficulty(), "EASY") || !Objects.equals(question.getDifficulty(), "MEDIUM")){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Failed to add question","Enter Valid Difficulty"));
        }
        try{
            Question newQuestion = new Question(question);
            questionService.createQuestion(newQuestion);
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Question added successfully", newQuestion.getQuestion()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to add question",e.getMessage()));
        }
    }

    @GetMapping("/retrieve")
    public ResponseEntity<APIResponse<List<Question>>> getQuestions(@RequestParam String category, @RequestParam int numberOfEasy, @RequestParam int numberOfMedium, @RequestParam int numberOfDifficult){
        try{
            return ResponseEntity.ok().body(APIResponse.success("Questions Fetched",questionService.getQuestions(category, numberOfEasy, numberOfMedium, numberOfDifficult)));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching question", e.getMessage()));
        }
    }
}
