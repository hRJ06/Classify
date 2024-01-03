package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.*;
import com.Hindol.Classroom.Payload.AssignmentResponseDTO;
import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditAssignmentDTO;
import com.Hindol.Classroom.Repository.*;
import com.Hindol.Classroom.Service.AssignmentService;
import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentServiceImplementation implements AssignmentService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public AssignmentResponseDTO removeSubmission(Integer assignmentId, String email, String role) {
        if(role.equals("INSTRUCTOR")) {
            return new AssignmentResponseDTO("This is a route for Students",false);
        }
        User user = this.userRepository.findByEmail(email);
        try {
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable to fetch Assignment with ID " + assignmentId));
            if(assignment.getCourse().getEnrolledUsers().contains(user)) {
                List<Submission> submissions = assignment.getSubmissions();
                boolean flag = false;
                Submission toRemove = null;
                for(Submission submission : submissions) {
                    if(submission.getUser().equals(user)) {
                        toRemove = submission;
                        flag = true;
                        break;
                    }
                }
                if(flag) {
                    this.submissionRepository.delete(toRemove);
                    return new AssignmentResponseDTO("Successfully Removed Your submission",true);
                }
                else {
                    return new AssignmentResponseDTO("There were no submission of your for this Assignment",false);
                }
            }
            else {
                return new AssignmentResponseDTO("You are not enrolled in the Course to which this assignment belongs",false);
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return new AssignmentResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public AssignmentResponseDTO addSubmission(List<MultipartFile> files, Integer assignmentId, String email, String role) {
        try {
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable to fetch Assignment with ID " + assignmentId));
            User user = this.userRepository.findByEmail(email);
            if(role.equals("INSTRUCTOR")) {
                return new AssignmentResponseDTO("You must be a student in order to submit your submission",false);
            }
            List<Submission> submissionList = assignment.getSubmissions();
            for(Submission submission : submissionList) {
                if(submission.getUser().equals(user)) {
                    return new AssignmentResponseDTO("You have Already given your Submission",false);
                }
            }
            Course course = assignment.getCourse();
            if(course.getEnrolledUsers().contains(user)) {
                List<File> submissions = new ArrayList<>();
                for(MultipartFile file : files) {
                    String fileName = file.getOriginalFilename();
                    Map data = this.cloudinary.uploader().upload(file.getBytes(),Map.of());
                    String uploadedLink = (String) data.get("secure_url");
                    File submissionFile = new File();
                    submissionFile.setFileName(fileName);
                    submissionFile.setFilePath(uploadedLink);
                    File savedFile = this.fileRepository.save(submissionFile);
                    submissions.add(savedFile);
                }
                Submission submission = new Submission();
                submission.setUser(user);
                submission.setFile(submissions);
                submission.setAssignment(assignment);
                LocalDateTime currentDateTime = LocalDateTime.now();
                submission.setSubmissionDateTime(currentDateTime);
                submission.setLateStatus(currentDateTime.compareTo(assignment.getDeadline()) > 0);
                Submission savedSubmission = this.submissionRepository.save(submission);
                assignment.getSubmissions().add(savedSubmission);
                user.getSubmissions().add(submission);
                this.userRepository.save(user);
                this.assignmentRepository.save(assignment);
                return new AssignmentResponseDTO("Successfully Added Your Submission To Assignment",true);
            }
            else {
                return new AssignmentResponseDTO("You need to first enroll in this Course in order to submit a submission for this assignment",false);
            }
        }
        catch (Exception e) {
            return new AssignmentResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public AssignmentSubmissionDTO getAllSubmission(String email, String role, Integer assignmentId) {
        User user = this.userRepository.findByEmail(email);
        if(role.equals("STUDENT")) {
            return new AssignmentSubmissionDTO(null,"You must be an Instructor in order to view all submission for an assignment",false);
        }
        try {
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable to fetch Assignment with ID " + assignmentId));
            if(assignment.getCourse().getInstructor().equals(user)) {
                List<Submission> submissions = assignment.getSubmissions();
                return new AssignmentSubmissionDTO(submissions,"Successfully fetched all submission for Assignment " + assignmentId,true);
            }
            else {
                return new AssignmentSubmissionDTO(null,"You need to be an instructor for the Course in order to view assignment submissions for it",false);
            }

        }
        catch (Exception e) {
            return new AssignmentSubmissionDTO(null,e.getMessage(),false);
        }
    }

    @Override
    public AssignmentResponseDTO editAssignment(EditAssignmentDTO editAssignmentDTO, String email, String role, Integer assignmentId) {
        try {
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable to Fetch Assignment With ID " + assignmentId));
            if(assignment.getCourse().getInstructor().getEmail().equals(email)) {
                assignment.setAssignmentName(editAssignmentDTO.getAssignmentName());
                assignment.setDescription(editAssignmentDTO.getDescription());
                /* STRING TO LOCAL DATE-TIME */
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime deadline = LocalDateTime.parse(editAssignmentDTO.getDeadline(), formatter);
                assignment.setDeadline(deadline);
                assignment.setFullMarks(editAssignmentDTO.getFullMarks());
                List<Submission> submissions = assignment.getSubmissions();
                for(Submission submission : submissions) {
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    submission.setLateStatus(currentDateTime.compareTo(assignment.getDeadline()) > 0);
                }
                this.assignmentRepository.save(assignment);
                return new AssignmentResponseDTO("Successfully Edited Assignment",true);
            }
            else {
                return new AssignmentResponseDTO("You must be an Instructor for this Course to edit thie Assignment",false);
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return new AssignmentResponseDTO(e.getMessage(),false);
        }
    }

}
