package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.AssignmentResponseDTO;
import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Service.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignment")
public class AssignmentController {
    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/submitSubmission/{assignmentId}")
    public ResponseEntity<AssignmentResponseDTO> submitSubmission(@RequestParam("files") List<MultipartFile> files, @PathVariable Integer assignmentId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        AssignmentResponseDTO assignmentResponseDTO = this.assignmentService.addSubmission(files,assignmentId,email,role);
        if(assignmentResponseDTO.getSuccess()) {
            return new ResponseEntity<AssignmentResponseDTO>(assignmentResponseDTO, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<AssignmentResponseDTO>(assignmentResponseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/allSubmission/{assignmentId}")
    public ResponseEntity<AssignmentSubmissionDTO> getAllSubmission(@PathVariable Integer assignmentId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        AssignmentSubmissionDTO assignmentSubmissionDTO = this.assignmentService.getAllSubmission(email,role,assignmentId);
        if(assignmentSubmissionDTO.getSuccess()) {
            return new ResponseEntity<AssignmentSubmissionDTO>(assignmentSubmissionDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<AssignmentSubmissionDTO>(assignmentSubmissionDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/removeSubmission/{assignmentId}")
    public ResponseEntity<AssignmentResponseDTO> removeSubmission(@PathVariable Integer assignmentId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        AssignmentResponseDTO assignmentResponseDTO = this.assignmentService.removeSubmission(assignmentId,email,role);
        if(assignmentResponseDTO.getSuccess()) {
            return new ResponseEntity<AssignmentResponseDTO>(assignmentResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<AssignmentResponseDTO>(assignmentResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
}