package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    User findByResetPasswordToken(String resetPasswordToken);
}
