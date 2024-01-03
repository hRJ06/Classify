package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.User;
import com.Hindol.Classroom.Payload.*;
import com.Hindol.Classroom.Repository.UserRepository;
import com.Hindol.Classroom.Service.UserService;
import com.Hindol.Classroom.Util.JWTToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JWTToken jwtToken;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    private String getToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenByte = new byte[8];
        secureRandom.nextBytes(tokenByte);
        String hexToken = new String(Hex.encode(tokenByte));
        return hexToken.substring(0, 8);
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        String email = userDTO.getEmail();
        User existinhUser = this.userRepository.findByEmail(email);
        if (existinhUser != null) {
            return null;
        } else {
            String hashedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());
            userDTO.setPassword(hashedPassword);
            User user = this.modelMapper.map(userDTO, User.class);
            this.userRepository.save(user);
            UserDTO responseUserDTO = this.modelMapper.map(user, UserDTO.class);
            return responseUserDTO;
        }
    }

    @Override
    public LoginResponseDTO loginUser(UserDTO userDTO) {
        String email = userDTO.getEmail();
        User existingUser = this.userRepository.findByEmail(email);
        if(existingUser != null) {
            if(bCryptPasswordEncoder.matches(userDTO.getPassword(), existingUser.getPassword())) {
                String token = jwtToken.generateToken(existingUser);
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
                loginResponseDTO.setToken(token);
                loginResponseDTO.setMessage("Login Success");
                loginResponseDTO.setRole(existingUser.getRole().toString());
                return loginResponseDTO;
            } else {
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
                loginResponseDTO.setToken(null);
                loginResponseDTO.setMessage("Password Don't Match");
                loginResponseDTO.setRole(null);
                return loginResponseDTO;
            }
        } else {
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
            loginResponseDTO.setToken(null);
            loginResponseDTO.setMessage("Login Failure");
            loginResponseDTO.setRole(null);
            return loginResponseDTO;
        }
    }

    @Override
    public TokenDTO generateToken(String email) {
        try {
            User user = this.userRepository.findByEmail(email);
            if(user != null) {
                String token = this.getToken();
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiryTime = now.plusMinutes(1);
                user.setResetPasswordToken(token);
                user.setResetPasswordTokenExpires(expiryTime);
                this.userRepository.save(user);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setFrom(sender);
                message.setSubject("Password Reset Link");
                String url = "https://localhost:3000/reset-password/" + token;
                message.setText("Please click on the URL {} to reset your password.".format(url));
                javaMailSender.send(message);
                return new TokenDTO("Successfully Send Reset Password Link to Email", true);
            } else {
                return new TokenDTO("Enter A Valid Email",true);
            }
        } catch (Exception e) {
            return new TokenDTO(e.getMessage(), false);
        }
    }
    @Override
    public TokenDTO resetPassword(ResetPasswordDTO resetPasswordDTO, String token) {
        if(!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            return new TokenDTO("Passwords do not match", false);
        }
        User user = this.userRepository.findByResetPasswordToken(token);
        if(user != null) {
            LocalDateTime expiryTime = user.getResetPasswordTokenExpires();
            LocalDateTime now = LocalDateTime.now();
            if (now.compareTo(expiryTime) > 0) {
                return new TokenDTO("The Token has expired. Please Generate a New token", false);
            }
            else {
                String hashedPassword = this.bCryptPasswordEncoder.encode(resetPasswordDTO.getPassword());
                user.setPassword(hashedPassword);
                this.userRepository.save(user);
                return new TokenDTO("Successfully Changed Password", true);
            }
        } else {
            return new TokenDTO("Please provide a valid token", false);
        }
    }

    @Override
    public UserDTO getDetails(String token) {
        try {
            TokenValidationResultDTO tokenValidationResultDTO = this.jwtToken.verifyToken(token);
            if("Expired Token".equals(tokenValidationResultDTO.getResult()) || "Invalid Token".equals(tokenValidationResultDTO.getResult())) {
                return null;
            }
            else {
                String email = tokenValidationResultDTO.getEmail();
                User user = this.userRepository.findByEmail(email);
                if(user != null) {
                    UserDTO userDTO = this.modelMapper.map(user,UserDTO.class);
                    /* FOR SECURITY PURPOSE */
                    userDTO.setPassword(null);
                    return userDTO;
                }else {
                    return null;
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
