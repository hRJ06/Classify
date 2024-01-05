package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Integer> {
}
