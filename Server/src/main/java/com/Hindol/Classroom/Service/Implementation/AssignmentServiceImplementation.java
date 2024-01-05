package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.*;
import com.Hindol.Classroom.Payload.AssignmentResponseDTO;
import com.Hindol.Classroom.Payload.AssignmentSubmissionDTO;
import com.Hindol.Classroom.Payload.EditAssignmentDTO;
import com.Hindol.Classroom.Repository.*;
import com.Hindol.Classroom.Service.AssignmentService;
import com.cloudinary.Cloudinary;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
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
    @Autowired
    private PrivateChatRepository privateChatRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") private String sender;

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

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkSubmission() {
        try {
            List<Assignment> assignmentList = this.assignmentRepository.findAll();
            LocalDateTime currentDateTime = LocalDateTime.now();
            for(Assignment assignment : assignmentList) {
                LocalDateTime deadline = assignment.getDeadline();
                boolean isPastDeadline = currentDateTime.isAfter(deadline);
                long hoursDifference  = ChronoUnit.HOURS.between(currentDateTime,deadline);
                if(isPastDeadline || !(hoursDifference <= 24)) continue;
                List<Submission> submissions = assignment.getSubmissions();
                List<User> enrolledUser = assignment.getCourse().getEnrolledUsers();
                List<User> userSubmit = new ArrayList<>();
                for(Submission submission : submissions) {
                    userSubmit.add(submission.getUser());
                }
                List<User> toSendEmail = new ArrayList<>();
                for(User user : enrolledUser) {
                    if(userSubmit.contains(user)) continue;
                    toSendEmail.add(user);
                }
                for(User user : toSendEmail) {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");
                    helper.setFrom(sender);
                    helper.setTo(user.getEmail());
                    String emailContent = String.format("""
                                                        <html>
                                                        <head>
                                                            <style>
                                                                body {
                                                                    font-family: 'Arial', sans-serif;
                                                                    background-color: #f0f8ff; /* Light Blue background */
                                                                    color: #006400; /* Dark Green text color */
                                                                    padding: 20px;
                                                                }
                                                                p {
                                                                    margin-bottom: 10px;
                                                                }
                                                            </style>
                                                        </head>
                                                        <body>
                                                            <p>Dear %s,</p>
                                                            <p>This is a reminder that the submission for the assignment "%s" in the course "%s" is due soon.</p>
                                                            <p>Please ensure that you submit your work on time.</p>
                                                            <p>Best regards,<br>Your Organization</p>
                                                        </body>
                                                        </html>
                                                        """, user.getFirstName() + " " + user.getLastName(), assignment.getAssignmentName(), assignment.getCourse().getCourseName());

                    helper.setText(emailContent, true);
                    helper.setSubject("Reminder! Submission Due For " + assignment.getAssignmentName() + " For Course " + assignment.getCourse().getCourseName());
                    javaMailSender.send(mimeMessage);
                }
            }
        }
        catch (Exception e) {
            log.error("An error occurred while sending reminder emails", e);
        }
    }

    @Override
    public PrivateChat createChat(String email, String role,Integer assignmentId,Integer studentId) {
        try {
            User user = this.userRepository.findByEmail(email);
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable to find Assignment with ID " + assignmentId));
            if(user != null) {
                if(role.equals("STUDENT")) {
                    Optional<PrivateChat> optionalPrivateChat = this.privateChatRepository.findByUserAndAssignment(user,assignment);
                    if(optionalPrivateChat.isEmpty()) {
                        PrivateChat privateChat = new PrivateChat();
                        privateChat.setUser(user);
                        privateChat.setAssignment(assignment);
                        PrivateChat savedPrivateChat = this.privateChatRepository.save(privateChat);
                        assignment.getPrivateChats().add(savedPrivateChat);
                        return savedPrivateChat;
                    }
                    else {
                        return optionalPrivateChat.get(); /* CHAT IS ALREADY THERE */
                    }
                }
                else {
                    /* FETCH STUDENT TO SET USER */
                    User student = this.userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Unable to fetch Student With ID " + studentId));
                    Optional<PrivateChat> optionalPrivateChat = this.privateChatRepository.findByUserAndAssignment(student,assignment);
                    if(optionalPrivateChat.isEmpty()) {
                        PrivateChat privateChat = new PrivateChat();
                        privateChat.setUser(student);
                        privateChat.setAssignment(assignment);
                        PrivateChat savedPrivateChat = this.privateChatRepository.save(privateChat);
                        assignment.getPrivateChats().add(savedPrivateChat);
                        return savedPrivateChat;
                    }
                    else {
                        return optionalPrivateChat.get(); /* CHAT IS ALREADY THERE */
                    }
                }
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
