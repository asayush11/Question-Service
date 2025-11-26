package com.example.questions_service.Controller;
import com.example.questions_service.Exception.InvalidUserException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginValidationResult>>  authenticateUser(@Valid @RequestBody UserDTO user, HttpServletResponse response){
        try {
            logger.info("Controller: Attempting to log in user with email: {}", user.getEmail());
            return ResponseEntity.ok().body(APIResponse.success("Login successful", userService.login(user.getEmail(), user.getPassword())));
        } catch (InvalidUserException e) {
            logger.error("Controller: Login failed for user with email: {}: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Login unsuccessful", "Invalid Credentials"));
        } catch (Exception e) {
            logger.error("Controller: Server error during login for user with email: {}: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>>  logoutUser(@RequestParam String token){
        logger.info("Controller: Logging out user with token: {}", token);
        userService.logout(token);
        logger.info("Controller: Logout successful for token: {}", token);
        return ResponseEntity.ok().body(APIResponse.success("Logout successful","Logout successful"));
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Boolean>>  createUser(@Valid @RequestBody UserDTO user){
       try{
           logger.info("Controller: Attempting to create user with email: {}", user.getEmail());
           if(userService.createUser(user.getUsername(), user.getEmail(), user.getPassword())){
               return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success("Account created", true));
           } else {
               logger.error("Controller: User creation failed - account with email already exists: {}", user.getEmail());
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(APIResponse.error("User with this email exists", "User with this email exists"));
           }
       } catch (Exception e) {
           logger.error("Controller: Server error during user creation for email: {}: {}", user.getEmail(), e.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
       }
    }

    @PostMapping("/change-password")
    public ResponseEntity<APIResponse<Boolean>>  changePassword(@RequestParam String email, @RequestParam String password){
        try {
            logger.warn("Controller: Attempting to change password for user with email: {}", email);
            return ResponseEntity.ok().body(APIResponse.success("Password changed", userService.changePassword(email, password)));
        } catch (InvalidUserException e) {
            logger.error("Controller: Password change failed for user with email: {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("User Not Found", "User Not Found"));
        } catch (Exception e) {
            logger.error("Controller: Server error during password change for user with email: {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<APIResponse<Void>>  deleteAccount(@RequestParam String email){
        try {
            logger.warn("Controller: Attempting to delete account for user with email: {}", email);
            userService.deleteAccount(email);
            logger.info("Controller: Account deletion successful for user with email: {}", email);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Controller: Server error during account deletion for user with email: {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Server Issue", e.getMessage()));
        }
    }
}
