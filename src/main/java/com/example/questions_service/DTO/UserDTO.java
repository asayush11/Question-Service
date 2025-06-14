package com.example.questions_service.DTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDTO {
    @NotNull(message = "Enter valid username")
    @Size(min = 1)
    private String username;
    @Email(message = "Enter valid email")
    @Size(min = 1)
    private String email;
    @NotNull(message = "Enter valid password")
    @Size(min = 1)
    private String password;

    public UserDTO() {
    }

    public UserDTO(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
