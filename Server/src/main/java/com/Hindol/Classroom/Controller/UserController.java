package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.*;
import com.Hindol.Classroom.Service.UserService;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = this.userService.registerUser(userDTO);
        if(createdUser != null) {
            return new ResponseEntity<UserDTO>(createdUser, HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<UserDTO>(createdUser, HttpStatus.OK);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody UserDTO userDTO) {
        LoginResponseDTO loginResponseDTO = this.userService.loginUser(userDTO);
        if(loginResponseDTO != null) {
            return new ResponseEntity<LoginResponseDTO>(loginResponseDTO, HttpStatus.ACCEPTED);
        }
        else {
            return new ResponseEntity<LoginResponseDTO>(loginResponseDTO, HttpStatus.NOT_ACCEPTABLE);
        }
    }
    @PostMapping("/generateResetPasswordToken")
    public ResponseEntity<?> generateToken(@RequestBody ResetPasswordTokenDTO resetPasswordTokenDTO) {
        String email = resetPasswordTokenDTO.getEmail();
        TokenDTO tokenDTO = this.userService.generateToken(email);
        if(tokenDTO != null) {
            return new ResponseEntity<TokenDTO>(tokenDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<TokenDTO>(tokenDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO, @PathVariable String token) {
        TokenDTO tokenDTO = this.userService.resetPassword(resetPasswordDTO,token);
        if(tokenDTO != null) {
            return new ResponseEntity<TokenDTO>(tokenDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<TokenDTO>(tokenDTO, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/my-details")
    public ResponseEntity<?> getDetails(@RequestHeader("Authorization") String bearerToken) {
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            UserDTO userDTO = this.userService.getDetails(token);
            return new ResponseEntity<UserDTO>(userDTO,HttpStatus.OK);
        }
        else {
            return ResponseEntity.badRequest().body("Provide a Valid Token");
        }

    }
}
