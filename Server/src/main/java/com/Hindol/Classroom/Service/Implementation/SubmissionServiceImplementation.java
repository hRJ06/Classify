package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.Assignment;
import com.Hindol.Classroom.Entity.Submission;
import com.Hindol.Classroom.Entity.User;
import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditCommentResponseDTO;
import com.Hindol.Classroom.Payload.EditMarksResponseDTO;
import com.Hindol.Classroom.Repository.AssignmentRepository;
import com.Hindol.Classroom.Repository.SubmissionRepository;
import com.Hindol.Classroom.Repository.UserRepository;
import com.Hindol.Classroom.Service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImplementation implements SubmissionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Override
    public AssignmentSubmissionDTO getSubmission(String email,String role) {
        User user = this.userRepository.findByEmail(email);
        if(role.equals("INSTRUCTOR")) {
            return new AssignmentSubmissionDTO(null,"You must be student in order to have submission",false);
        }
        else {
          List<Submission> submissionList = user.getSubmissions();
          return new AssignmentSubmissionDTO(submissionList,"Successfully fetched all submissions",true);
        }
    }

    @Override
    public AssignmentSubmissionDTO getAssignmentSubmission(String email, String role, Integer assignmentId) {
        User user = this.userRepository.findByEmail(email);
        if(role.equals("INSTRUCTOR")) {
            return new AssignmentSubmissionDTO(null,"Only a Student can get submission for an Assignment from here",false);
        }
        try {
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable to fetch Assignment with ID " + assignmentId));
            if(assignment.getCourse().getEnrolledUsers().contains(user)) {
                List<Submission> allSubmission = assignment.getSubmissions();
                List<Submission> userSubmission = new ArrayList<>();
                for(Submission submission : allSubmission) {
                    if(submission.getUser().equals(user)) {
                        userSubmission.add(submission);
                    }
                }
                return new AssignmentSubmissionDTO(userSubmission,"Successfully fetched submission for User for Assignment " + assignmentId,true);
            }
            else {
                return new AssignmentSubmissionDTO(null,"You must enroll in Course to submit / fetch submission for this Assignemnt",false);
            }
        }
        catch (Exception e) {
            return new AssignmentSubmissionDTO(null,e.getMessage(),false);
        }
    }

    @Override
    public EditMarksResponseDTO editSubmissionMarks(String email, String role, Integer submissionId,Integer marks) {
        try {
            if(role.equals("STUDENT")) {
                return null;
            }
            Submission submission = this.submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("Unable to fetch Submission with ID " + submissionId));
            int totalMarks = submission.getAssignment().getFullMarks();
            if(marks > totalMarks) {
                return new EditMarksResponseDTO("Marks Should Be Within " + totalMarks,false);
            }
            submission.setMarks(marks);
            this.submissionRepository.save(submission);
            return new EditMarksResponseDTO("Marks Added",true);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public EditCommentResponseDTO editSubmissionComment(String email, String role, Integer submissionId, String comment) {
        try {
            if(role.equals("STUDENT")) {
                return new EditCommentResponseDTO("You must be an INSTRUCTOR",false);
            }
            Submission submission = this.submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("Unable to fetch Submission with ID " + submissionId));
            submission.setComment(comment);
            this.submissionRepository.save(submission);
            return new EditCommentResponseDTO("Successfully Added Comment To Submission",true);
        }
        catch (Exception e) {
            return new EditCommentResponseDTO(e.getMessage(),false);
        }
    }
}
