package com.example.questions_service.DTO;

import java.util.List;

public class AnswerResponseDTO {
    private final List<AnswerKeyDTO> answers;
    private final Integer totalQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Double percentageScore;

    public AnswerResponseDTO(List<AnswerKeyDTO> answers, Integer totalQuestions) {
        this.answers = answers;
        this.totalQuestions = totalQuestions;
    }

    public List<AnswerKeyDTO> getAnswers() {
        return answers;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public Integer getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public Double getPercentageScore() {
        return percentageScore;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public void setIncorrectAnswers(Integer incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public void setPercentageScore(Double percentageScore) {
        this.percentageScore = percentageScore;
    }
}
