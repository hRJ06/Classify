package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditMarksRequestDTO;
import com.Hindol.Classroom.Payload.EditMarksResponseDTO;
import com.Hindol.Classroom.Service.SubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;
    @GetMapping("/getSubmission")
    public ResponseEntity<AssignmentSubmissionDTO> getAllSubmission(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        AssignmentSubmissionDTO assignmentSubmissionDTO = this.submissionService.getSubmission(email,role);
        if(assignmentSubmissionDTO.getSuccess()) {
            return new ResponseEntity<>(assignmentSubmissionDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(assignmentSubmissionDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getSubmission/{assignmentId}")
    public ResponseEntity<AssignmentSubmissionDTO> getSubmission(@PathVariable Integer assignmentId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        AssignmentSubmissionDTO assignmentSubmissionDTO = this.submissionService.getAssignmentSubmission(email,role,assignmentId);
        if(assignmentSubmissionDTO.getSuccess()) {
            return new ResponseEntity<AssignmentSubmissionDTO>(assignmentSubmissionDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<AssignmentSubmissionDTO>(assignmentSubmissionDTO,HttpStatus.BAD_GATEWAY);
        }
    }
    @PostMapping("/editMarks/{submissionId}")
    public ResponseEntity<?> editSubmissionMarks(@PathVariable Integer submissionId, HttpServletRequest request, @RequestBody EditMarksRequestDTO editMarksRequestDTO) {
        String role = (String) request.getAttribute("Role");
        String email = (String) request.getAttribute("email");
        Integer marks = editMarksRequestDTO.getMarks();
        EditMarksResponseDTO editMarksResponseDTO = this.submissionService.editSubmissionMarks(email,role,submissionId,marks);
        if(editMarksResponseDTO != null) {
            return new ResponseEntity<EditMarksResponseDTO>(editMarksResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<EditMarksResponseDTO>(editMarksResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
}
