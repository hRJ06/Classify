package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Assignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseAssignmentResponseDTO {
    private List<Assignment> assignmentList;
    private String message;
    private Boolean success;
}
