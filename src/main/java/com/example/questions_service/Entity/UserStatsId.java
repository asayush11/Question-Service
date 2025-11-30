package com.example.questions_service.Entity;

import java.io.Serializable;
import java.util.Objects;

public class UserStatsId implements Serializable {

    private String quizId;
    private String emailID;
    public UserStatsId() {
    }
    public UserStatsId(String quizId, String emailID) {
        this.quizId = quizId;
        this.emailID = emailID;
    }
    public String getQuizId() {
        return quizId;
    }
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    public String getEmailID() {
        return emailID;
    }
    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
    // MUST implement equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStatsId that = (UserStatsId) o;
        return Objects.equals(quizId, that.getQuizId()) &&
                Objects.equals(emailID, that.getEmailID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, emailID);
    }
}
