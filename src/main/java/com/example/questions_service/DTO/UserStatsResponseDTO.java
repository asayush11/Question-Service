package com.example.questions_service.DTO;

import java.time.LocalDateTime;

public class UserStatsResponseDTO {
    private String quizId;
    private String quizType;
    private Double percentageScore;
    private LocalDateTime dateTaken;

    public UserStatsResponseDTO() {
    }

    public UserStatsResponseDTO(String quizId, String quizType, Double percentageScore, LocalDateTime dateTaken) {
        this.quizId = quizId;
        this.quizType = quizType;
        this.percentageScore = percentageScore;
        this.dateTaken = dateTaken;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getQuizType() {
        return quizType;
    }

    public Double getPercentageScore() {
        return percentageScore;
    }

    public LocalDateTime getDateTaken() {
        return dateTaken;
    }
}
