package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Entity.PrivateChat;
import com.Hindol.Classroom.Payload.AssignmentResponseDTO;
import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditAssignmentDTO;
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
    @PutMapping("/editAssignment/{assignmentId}")
    public ResponseEntity<?> editAssignment(@PathVariable Integer assignmentId, HttpServletRequest request, @RequestBody EditAssignmentDTO editAssignmentDTO) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        AssignmentResponseDTO assignmentResponseDTO = this.assignmentService.editAssignment(editAssignmentDTO,email,role,assignmentId);
        if(assignmentResponseDTO.getSuccess()) {
            return new ResponseEntity<AssignmentResponseDTO>(assignmentResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<AssignmentResponseDTO>(assignmentResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/check-submission")
    public ResponseEntity<?> checkSubmission() {
        try {
            this.assignmentService.checkSubmission();
            return ResponseEntity.ok("Executed");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal Server Error");
        }
    }
    @PostMapping("/create-chat/{assignmentId}")
    public ResponseEntity<?> createChat(HttpServletRequest request,@PathVariable Integer assignmentId,@RequestParam(name = "StudentId", required = false) Integer studentId) {
        try {
            String email = (String) request.getAttribute("email");
            String role = (String) request.getAttribute("Role");
            PrivateChat privateChat = this.assignmentService.createChat(email,role,assignmentId,studentId);
            if(privateChat != null) {
                return ResponseEntity.ok("Chat Created");
            }
            else {
                return ResponseEntity.badRequest().body("Internal Server Error");
            }
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Internal Server Error");
        }
    }
}
