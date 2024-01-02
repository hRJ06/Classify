package com.Hindol.Classroom.Middleware;

import com.Hindol.Classroom.Payload.TokenValidationResultDTO;
import com.Hindol.Classroom.Util.JWTToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private JWTToken jwtToken;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");
        try {
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                TokenValidationResultDTO tokenValidationResultDTO = this.jwtToken.verifyToken(token);
                if("Expired Token".equals(tokenValidationResultDTO.getResult()) || "Invalid Token".equals(tokenValidationResultDTO.getResult())) {
                    log.warn("Token Validation failed: {}", tokenValidationResultDTO.getResult());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid Token");
                    return false;
                }
                else {
                    log.info("Token Validation Successful: {}", tokenValidationResultDTO.getResult());
                    request.setAttribute("email",tokenValidationResultDTO.getEmail());
                    request.setAttribute("Role",tokenValidationResultDTO.getRole());
                    return true;
                }
            }
            else {
                log.warn("Authorization Header is Missing");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Header Missing");
                return false;
            }
        }
        catch (Exception ex) {
            log.error("Exception occured during Validation",ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Internal Server Error");
            return false;
        }
    }
}
