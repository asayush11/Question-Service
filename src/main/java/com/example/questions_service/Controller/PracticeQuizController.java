package com.example.questions_service.Controller;

import com.example.questions_service.DTO.AnswerRequestDTO;
import com.example.questions_service.DTO.AnswerResponseDTO;
import com.example.questions_service.DTO.PracticeQuizRequestDTO;
import com.example.questions_service.DTO.QuizDTO;
import com.example.questions_service.Service.QuizService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/practice-quiz")
public class PracticeQuizController {

    @Autowired
    QuizService quizService;
    private static final Logger logger = LoggerFactory.getLogger(PracticeQuizController.class);

    @GetMapping("/questions")
    public ResponseEntity<APIResponse<QuizDTO>> getQuestions(@RequestParam String email, @Valid @RequestBody PracticeQuizRequestDTO request) {
        logger.info("Controller: Fetching questions for quiz");
        if(request.getNumberOfQuestions() < 0){
            logger.error("Controller: Invalid number of questions requested : {}", request.getNumberOfQuestions());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Invalid number of question", "Invalid number of question"));
        }
        try{
            return ResponseEntity.ok().body(APIResponse.success("Questions Fetched", quizService.getPracticeQuestions(request.getSubjects(), request.getDifficulty(), request.getNumberOfQuestions(), email)));
        } catch (Exception e){
            logger.error("Controller: Server Error fetching questions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching question", e.getMessage()));
        }
    }

    @GetMapping("/answers")
    public ResponseEntity<APIResponse<AnswerResponseDTO>> getAnswers(@RequestParam String email, @RequestBody AnswerRequestDTO request) {
        logger.info("Controller: Fetching answers for quizId: {}", request.getQuizId());
        if(request.getQuizId() == null || request.getQuizId().isEmpty()){
            logger.error("Controller: Invalid quizID provided: {}", request.getQuizId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("Invalid quizID", "Invalid quizID"));
        }
        try{
            return ResponseEntity.ok().body(APIResponse.success("Answers Fetched", quizService.getAnswers(request.getQuizId(), request.getAnswer(), email, "PRACTICE")));
        } catch (Exception e){
            logger.error("Controller: Server Error fetching answers for quizId {}: {}", request.getQuizId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching answers", e.getMessage()));
        }
    }
}
