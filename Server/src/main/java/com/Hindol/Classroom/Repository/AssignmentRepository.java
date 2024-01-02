package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment,Integer> {
}
