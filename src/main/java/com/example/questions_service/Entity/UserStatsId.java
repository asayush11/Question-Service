package com.example.questions_service.Entity;

import java.io.Serializable;
import java.util.Objects;

public class UserStatsId implements Serializable {

    private String quizId;
    private String emailId;
    public UserStatsId() {
    }
    public UserStatsId(String quizId, String emailId) {
        this.quizId = quizId;
        this.emailId = emailId;
    }
    public String getQuizId() {
        return quizId;
    }
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    public String getEmailId() {
        return emailId;
    }
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    // MUST implement equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStatsId that = (UserStatsId) o;
        return Objects.equals(quizId, that.getQuizId()) &&
                Objects.equals(emailId, that.getEmailId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, emailId);
    }
}
