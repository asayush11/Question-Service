package com.example.questions_service.Controller;

import com.example.questions_service.DTO.AnswerResponseDTO;
import com.example.questions_service.DTO.QuestionRequestDTO;
import com.example.questions_service.DTO.QuizDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Exception.UserNotAdminException;
import com.example.questions_service.Service.QuestionService;
import com.example.questions_service.Service.QuizService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    @GetMapping("/questions")
    public ResponseEntity<APIResponse<QuizDTO>> getQuestions(@RequestHeader("Authorization") String authHeader, @RequestParam String category, @RequestParam int numberOfEasy, @RequestParam int numberOfMedium, @RequestParam int numberOfDifficult){
        if(numberOfDifficult < 0 || numberOfEasy < 0 || numberOfMedium < 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Invalid number of question", "Invalid number of question"));
        }
        try{
            return ResponseEntity.ok().body(APIResponse.success("Questions Fetched", quizService.getQuestions(category, numberOfEasy, numberOfMedium, numberOfDifficult, authHeader)));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching question", e.getMessage()));
        }
    }

    @GetMapping("/answers")
    public ResponseEntity<APIResponse<List<AnswerResponseDTO>>> getAnswers(@RequestParam String quizId){
        if(quizId == null || quizId.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Invalid quizID", "Invalid quizID"));
        }
        try{
            return ResponseEntity.ok().body(APIResponse.success("Questions Fetched", quizService.getAnswers(quizId)));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching answers", e.getMessage()));
        }
    }
}
