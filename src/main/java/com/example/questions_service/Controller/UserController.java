package com.example.questions_service.Controller;
import jakarta.servlet.http.HttpServletResponse;
import com.example.questions_service.DTO.UserDTO;
import com.example.questions_service.Service.UserService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse<Boolean>>  authenticateUser(@Valid @RequestBody UserDTO user, HttpServletResponse response){
        if(userService.login(user.getEmail(), user.getPassword())){
            ResponseCookie emailCookie = ResponseCookie.from("email", user.getEmail())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(Duration.ofMinutes(10))
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, emailCookie.toString());
            return ResponseEntity.ok(APIResponse.success("Login successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Login unsuccessful", "Invalid Credentials"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Boolean>>  createUser(@Valid @RequestBody UserDTO user){
        if(userService.createUser(user.getUsername(), user.getEmail(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Account created", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("User with this email exists", "User with this email exists"));
        }
    }
}
