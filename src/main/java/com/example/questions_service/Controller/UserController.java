package com.example.questions_service.Controller;

import com.example.questions_service.DTO.UserDTO;
import com.example.questions_service.Repository.UserRepository;
import com.example.questions_service.Service.DBWakeUp;
import com.example.questions_service.Service.UserService;
import com.example.questions_service.Utility.APIResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    DBWakeUp dbWakeUp;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse<Boolean>>  authenticateUser(@Valid @RequestBody UserDTO user){
        if(userService.login(user.getEmail(), user.getPassword())){
            dbWakeUp.warmUpDb();
            return ResponseEntity.ok(APIResponse.success("Login successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Login unsuccessful", "Invalid Credentials"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Boolean>>  createUser(@Valid @RequestBody UserDTO user){
        dbWakeUp.warmUpDb();
        if(userService.createUser(user.getUsername(), user.getEmail(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Login successful", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("User exists", "User exists"));
        }
    }
}
