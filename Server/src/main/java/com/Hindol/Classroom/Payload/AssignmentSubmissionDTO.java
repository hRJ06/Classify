package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Submission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionDTO {
    private List<Submission> submissionList;
    private String message;
    private Boolean success;
}
