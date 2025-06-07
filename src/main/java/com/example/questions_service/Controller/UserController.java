package com.example.questions_service.Controller;

import com.example.questions_service.Utility.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @PostMapping("/login")
    public ResponseEntity<APIResponse<Boolean>>  authenticateUser(@PathVariable String password){
        if(password.equals("123456")){
            return ResponseEntity.ok(APIResponse.success("Login successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Login unsuccessful", "Invalid Password"));
        }
    }
}
