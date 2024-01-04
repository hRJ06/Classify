package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditCommentResponseDTO;
import com.Hindol.Classroom.Payload.EditMarksResponseDTO;

public interface SubmissionService {
    AssignmentSubmissionDTO getSubmission(String email, String role);
    /* USER */
    AssignmentSubmissionDTO getAssignmentSubmission(String email,String role,Integer assignmentId);
    EditMarksResponseDTO editSubmissionMarks(String email, String role, Integer submissionId,Integer marks);
    EditCommentResponseDTO editSubmissionComment(String email,String role,Integer submissionId,String comment);
}
