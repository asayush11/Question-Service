package com.example.questions_service.Entity;

import com.example.questions_service.DTO.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String username;
    private String emailID;
    private String password;

    public User() {
    }

    public User(Integer userId, String username, String emailID, String password) {
        this.userId = userId;
        this.username = username;
        this.emailID = emailID;
        this.password = password;
    }

    public User(UserDTO userDTO){
        this.username = userDTO.getUsername();
        this.emailID = userDTO.getEmail();
        this.password = userDTO.getPassword();
    }

    public User(String username, String emailID, String password) {
        this.username = username;
        this.emailID = emailID;
        this.password = password;
    }

    public Integer getUserId() {
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
}
