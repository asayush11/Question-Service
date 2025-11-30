package com.example.questions_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.time.LocalDateTime;

@Entity
@IdClass(UserStatsId.class)
public class UserStatistics {
    @Id
    private String quizId;
    @Id
    private String emailId;
    private String quizType;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer incorrectAnswers;
    private Double percentageScore;
    private LocalDateTime dateTaken;

    public UserStatistics() {}

    public UserStatistics(String quizId, String emailId, String quizType, Integer totalQuestions, Integer correctAnswers, Integer incorrectAnswers, Double percentageScore) {
        this.quizId = quizId;
        this.emailId = emailId;
        this.quizType = quizType;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.incorrectAnswers = incorrectAnswers;
        this.percentageScore = percentageScore;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getEmailId() {
        return emailId;
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

    public LocalDateTime getDateTaken() {
        return dateTaken;
    }
}
