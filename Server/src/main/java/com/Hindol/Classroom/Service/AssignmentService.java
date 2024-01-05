package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Entity.PrivateChat;
import com.Hindol.Classroom.Payload.AssignmentResponseDTO;
import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditAssignmentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentService {
    public AssignmentResponseDTO addSubmission(List<MultipartFile> files, Integer assignmentId,String email,String role);
    public AssignmentResponseDTO removeSubmission(Integer assignmentId,String email,String role);
    /* INSTRUCTOR */
    public AssignmentSubmissionDTO getAllSubmission(String email, String role, Integer assignmentId);
    public AssignmentResponseDTO editAssignment(EditAssignmentDTO editAssignmentDTO,String email,String role,Integer assignmentId);
    public void checkSubmission();
    public PrivateChat createChat(String email, String role, Integer assignmentId, Integer studentId);
}
