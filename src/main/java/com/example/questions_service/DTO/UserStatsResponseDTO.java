package com.example.questions_service.DTO;

public class UserStatsResponseDTO {
    private String quizId;
    private String quizType;
    private Double percentageScore;
    private String dateTaken;

    public UserStatsResponseDTO(String quizId, String quizType, Double percentageScore, String dateTaken) {
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

    public String getDateTaken() {
        return dateTaken;
    }
}
