package com.example.questions_service.DTO;

import java.util.List;

public class AnswerKeyDTO {
    private final String solution;
    private final List<String> answerKey;

    public AnswerKeyDTO(String solution, List<String> answerKey) {
        this.solution = solution;
        this.answerKey = answerKey;
    }

    public String getSolution() {
        return solution;
    }

    public List<String> getAnswerKey() {
        return answerKey;
    }
}
