package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.Assignment;
import com.Hindol.Classroom.Entity.PrivateChat;
import com.Hindol.Classroom.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivateChatRepository extends JpaRepository<PrivateChat,Integer> {
    Optional<PrivateChat> findByUserAndAssignment(User user, Assignment assignment);
}
