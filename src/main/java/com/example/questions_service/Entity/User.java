package com.example.questions_service.Entity;

import com.example.questions_service.DTO.UserDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long userId;
    private String username;
    @Column(name = "email_id", unique = true)
    private String emailID;
    private String password;
    private Integer quizzesTaken;
    private Integer questionsContributed;

    public User() {
    }

    public User(Long userId, String username, String emailID, String password, Integer questionsContributed, Integer quizzesTaken) {
        this.userId = userId;
        this.username = username;
        this.emailID = emailID;
        this.password = password;
        this.quizzesTaken = quizzesTaken;
        this.questionsContributed = questionsContributed;
    }

    public User(UserDTO userDTO){
        this.username = userDTO.getUsername();
        this.emailID = userDTO.getEmail();
        this.password = userDTO.getPassword();
        this.quizzesTaken = 0;
        this.questionsContributed = 0;
    }

    public User(String username, String emailID, String password) {
        this.username = username;
        this.emailID = emailID;
        this.password = password;
        this.quizzesTaken = 0;
        this.questionsContributed = 0;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getPassword() {
        return password;
    }

    public Integer getQuizzesTaken() {
        return quizzesTaken;
    }

    public void setQuizzesTaken(Integer quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }

    public Integer getQuestionsContributed() {
        return questionsContributed;
    }

    public void setQuestionsContributed(Integer numberOfQuestions) {
        this.questionsContributed = numberOfQuestions;
    }
}
