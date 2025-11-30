package com.example.questions_service.DTO;

import java.util.List;

public class AnswerRequestDTO {
    private final String quizId;
    private final List<List<String>> answer;
    public AnswerRequestDTO(String quizId, List<List<String>> answer) {
        this.quizId = quizId;
        this.answer = answer;
    }
    public String getQuizId() {
        return quizId;
    }
    public List<List<String>> getAnswer() {
        return answer;
    }
}
