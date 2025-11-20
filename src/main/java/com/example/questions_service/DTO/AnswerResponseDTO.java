package com.example.questions_service.DTO;

import java.util.List;

public class AnswerResponseDTO {
    private final List<String> answer;
    private final String solution;

    public AnswerResponseDTO(List<String> answer, String solution) {
        this.answer = answer;
        this.solution = solution;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public String getSolution() {
        return solution;
    }
}
