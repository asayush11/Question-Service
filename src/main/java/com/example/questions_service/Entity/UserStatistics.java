package com.example.questions_service.Entity;

import jakarta.persistence.Entity;

@Entity
public class UserStatistics {
    private String quizId;
    private String emailID;
    private String quizType;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Double percentageScore;
    private String dateTaken;

    public UserStatistics(String quizId, String emailID, String quizType, Integer totalQuestions, Integer correctAnswers, Integer incorrectAnswers, Double percentageScore) {
        this.quizId = quizId;
        this.emailID = emailID;
        this.quizType = quizType;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.percentageScore = percentageScore;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getQuizType() {
        return quizType;
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

    public String getDateTaken() {
        return dateTaken;
    }
}
