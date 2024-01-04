package com.Hindol.Classroom.Configuration;

import com.Hindol.Classroom.Middleware.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/api/v1/course/**");
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/api/v1/assignment/**").excludePathPatterns("/api/v1/assignment/check-submission");
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/api/v1/submission/**");
        registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/api/v1/file/**");
    }
}
