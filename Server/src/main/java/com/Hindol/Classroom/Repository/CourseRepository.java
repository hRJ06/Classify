package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course,Integer> {
    Course findByCode(String code);
}
