package com.example.questions_service.Controller;
import com.example.questions_service.Utility.LoginValidationResult;
import jakarta.servlet.http.HttpServletResponse;
import com.example.questions_service.DTO.UserDTO;
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
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginValidationResult>>  authenticateUser(@Valid @RequestBody UserDTO user, HttpServletResponse response){
        try {
            return ResponseEntity.ok().body(APIResponse.success("Login successful", userService.login(user.getEmail(), user.getPassword())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Login unsuccessful", "Invalid Credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>>  logoutUser(@RequestParam String token){
        userService.logout(token);
        return ResponseEntity.ok().body(APIResponse.success("Logout successful","Logout successful"));
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Boolean>>  createUser(@Valid @RequestBody UserDTO user){
       try{
           if(userService.createUser(user.getUsername(), user.getEmail(), user.getPassword())){
               return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Account created", true));
           } else {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("User with this email exists", "User with this email exists"));
           }
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
       }
    }

    @PostMapping("/change-password")
    public ResponseEntity<APIResponse<Boolean>>  changePassword(@RequestParam String email, @RequestParam String password){
        try {
            return ResponseEntity.ok().body(APIResponse.success("Password changed", userService.changePassword(email, password)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("User Not Found", "User Not Found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<APIResponse<Void>>  deleteAccount(@RequestParam String email){
        try {
            userService.deleteAccount(email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
        }
    }
}
