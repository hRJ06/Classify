package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.LoginResponseDTO;
import com.Hindol.Classroom.Payload.ResetPasswordDTO;
import com.Hindol.Classroom.Payload.TokenDTO;
import com.Hindol.Classroom.Payload.UserDTO;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    LoginResponseDTO loginUser(UserDTO userDTO);
    TokenDTO generateToken(String email);
    TokenDTO resetPassword(ResetPasswordDTO resetPasswordDTO, String token);
}
