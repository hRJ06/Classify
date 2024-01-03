package com.Hindol.Classroom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditAssignmentDTO {
    private String assignmentName;
    private String deadline;
    private String description;
    private Integer fullMarks;
}
