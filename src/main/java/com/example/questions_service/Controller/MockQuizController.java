package com.example.questions_service.Controller;

import com.example.questions_service.DTO.AnswerRequestDTO;
import com.example.questions_service.DTO.AnswerResponseDTO;
import com.example.questions_service.DTO.QuizDTO;
import com.example.questions_service.Service.QuizService;
import com.example.questions_service.Utility.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock-quiz")
public class MockQuizController {
    @Autowired
    QuizService quizService;
    private static final Logger logger = LoggerFactory.getLogger(MockQuizController.class);

    @GetMapping("/questions/{quizId}")
    public ResponseEntity<APIResponse<QuizDTO>> getQuestions(@RequestParam String email, @PathVariable String quizId) {
        logger.info("Controller: Fetching questions for mock quiz: {}", quizId);
        try{
            QuizDTO quiz = quizService.getMockQuiz(email, quizId);
            if(quiz == null){
                logger.error("Controller: No live quiz available currently");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Mock quiz not found", "Mock quiz not found"));
            }
            return ResponseEntity.ok().body(APIResponse.success("Questions Fetched", quiz));
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
            return ResponseEntity.ok().body(APIResponse.success("Answers Fetched", quizService.getAnswers(request.getQuizId(), request.getAnswer(), email, "MOCK")));
        } catch (Exception e){
            logger.error("Controller: Server Error fetching answers for quizId {}: {}", request.getQuizId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Error fetching answers", e.getMessage()));
        }
    }
}
