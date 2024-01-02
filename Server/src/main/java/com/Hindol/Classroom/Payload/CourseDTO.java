package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Integer id;
    private String courseName;
    private List<User> enrolledUsers;
    private String code;
}
