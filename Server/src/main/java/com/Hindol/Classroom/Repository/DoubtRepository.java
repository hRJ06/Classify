package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.Course;
import com.Hindol.Classroom.Entity.Doubt;
import com.Hindol.Classroom.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoubtRepository extends JpaRepository<Doubt,Integer> {
    List<Doubt> findByContentContainingAndCourseEquals(String keyword, Course course);
    List<Doubt> findByUserEquals(User user);
}
