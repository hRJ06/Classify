package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.*;
import com.Hindol.Classroom.Payload.CourseAnnouncementResponseDTO;
import com.Hindol.Classroom.Payload.CourseAssignmentResponseDTO;
import com.Hindol.Classroom.Payload.CourseDTO;
import com.Hindol.Classroom.Payload.CourseResponseDTO;
import com.Hindol.Classroom.Repository.*;
import com.Hindol.Classroom.Service.CourseService;
import com.cloudinary.Cloudinary;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImplementation implements CourseService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") private String sender;
    private static String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }
    @Override
    public List<CourseDTO> enrolledCourse(String email,String role) {
        User user = this.userRepository.findByEmail(email);
        if(role.equals("INSTRUCTOR")) {
            List<Course> courses = user.getCreatedCourses();
            List<CourseDTO> courseDTOS = courses.stream().map(course -> this.modelMapper.map(course,CourseDTO.class)).collect(Collectors.toList());
            return courseDTOS;
        }
        else {
            List<Course> courses = user.getEnrolledCourses();
            List<CourseDTO> courseDTOS = courses.stream().map(course -> this.modelMapper.map(course,CourseDTO.class)).collect(Collectors.toList());
            return courseDTOS;
        }
    }

    @Override
    public CourseResponseDTO enrollCourse(String email, String role, String courseCode) {
        if(role.equals("INSTRUCTOR")) {
            return new CourseResponseDTO("Only a Student is allowed to enroll in a course",false);
        }
        try {
            Course course = this.courseRepository.findByCode(courseCode);
            if(course != null) {
                User user = this.userRepository.findByEmail(email);
                if(user.getEnrolledCourses().contains(course)) {
                    return new CourseResponseDTO("User is Already Enrolled In Course",true);
                }
                user.getEnrolledCourses().add(course);
                course.getEnrolledUsers().add(user);
                this.userRepository.save(user);
                this.courseRepository.save(course);
                return new CourseResponseDTO("Successfully Added To Course",true);
            }
            else {
                throw new RuntimeException("Unable to Fetch Course With Code " + courseCode);
            }

        }
        catch (Exception e) {
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public CourseResponseDTO unrenrollCourse(String email, String role, Integer courseId) {
        User user = this.userRepository.findByEmail(email);
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to fetch Course with Course ID " + courseId));
            if(role.equals("INSTRUCTOR")) {
                return new CourseResponseDTO("Instructor cannot leave a Course",false);
            }
            else {
                if(course.getEnrolledUsers().contains(user)) {
                    course.getEnrolledUsers().remove(user);
                    user.getEnrolledCourses().remove(course);
                    this.userRepository.save(user);
                    this.courseRepository.save(course);
                    return new CourseResponseDTO("Successfully UnEnrolled from course",true);
                }
                else {
                    return new CourseResponseDTO("Please Enroll in course first",false);
                }
            }
        }
        catch (Exception e) {
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public CourseResponseDTO createAssignment(String email, String role, Integer courseId, List<MultipartFile> files, String name, String description, String deadlineString,String fullMarks) {
        User user = this.userRepository.findByEmail(email);
        try {
            if(role.equals("INSTRUCTOR")) {
                Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to fetch Course with Course ID " + courseId));
                if(course.getInstructor().equals(user)) {
                    Assignment assignment = new Assignment();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    LocalDateTime deadline = LocalDateTime.parse(deadlineString, formatter);
                    List<File> uploadfiles = new ArrayList<>();
                    for(MultipartFile file : files) {

                        String fileName = file.getOriginalFilename();
                        Map data = this.cloudinary.uploader().upload(file.getBytes(),Map.of());
                        String uploadedLink = (String) data.get("secure_url");

                        File assignmentFile = new File();
                        assignmentFile.setFileName(fileName);
                        assignmentFile.setFilePath(uploadedLink);
                        File savedFile = this.fileRepository.save(assignmentFile);
                        uploadfiles.add(savedFile);
                    }

                    assignment.setFile(uploadfiles);
                    assignment.setCourse(course);
                    assignment.setDescription(description);
                    assignment.setAssignmentName(name);
                    assignment.setDeadline(deadline);
                    assignment.setFullMarks(Integer.valueOf(fullMarks));
                    Assignment savedAssignment = this.assignmentRepository.save(assignment);
                    course.getAssignments().add(assignment);
                    this.courseRepository.save(course);
                    String htmlContent = """
                                        <html>
                                        <head>
                                            <style>
                                                .card {
                                                    background-color: #f9f9f9;
                                                    border: 1px solid #ddd;
                                                    border-radius: 8px;
                                                    padding: 20px;
                                                    width: 300px;
                                                    margin: 20px auto;
                                                }
                                                .fade-background {
                                                    background: linear-gradient(rgba(255,255,255,0), rgba(255,255,255,1));
                                                    padding: 10px;
                                                    border-radius: 8px;
                                                }
                                                .button {
                                                    display: inline-block;
                                                    padding: 10px 20px;
                                                    font-size: 16px;
                                                    text-align: center;
                                                    text-decoration: none;
                                                    background-color: #007BFF;
                                                    color: #fff;
                                                    border-radius: 5px;
                                                    cursor: pointer;
                                                }
                                            </style>
                                        </head>
                                        <body>
                                            <div class="card">
                                                <div class="fade-background">
                                                    <h2>%s</h2>
                                                    <p>Course: %s</p>
                                                    <a href="http://localhost:3000/assignment" class="button">Click to View</a>
                                                </div>
                                            </div>
                                        </body>
                                        </html>
                                        """.formatted(name, course.getCourseName());
                    for(User student : course.getEnrolledUsers()) {
                        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                        helper.setFrom(sender);
                        helper.setTo(student.getEmail());
                        helper.setText(htmlContent, true);
                        helper.setSubject("New Assignment for " + course.getCourseName());
                        javaMailSender.send(mimeMessage);

                    }
                    return new CourseResponseDTO("Successfully Added Assignment to Course",true);
                }
                else {
                    return new CourseResponseDTO("You are not an Instructor for this Course",false);
                }

            }
            else {
                return new CourseResponseDTO("You need to be an Instructor to add Assignment to Course",false);
            }

        }
        catch (Exception e) {
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public CourseAssignmentResponseDTO getAssignment(Integer courseId,String email,String role) {
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to fetch Course with ID " + courseId));
            List<Assignment> assignmentList = course.getAssignments();
            if(role.equals("STUDENT")) {
                for(Assignment a : assignmentList) {
                    List<Submission> submissions = a.getSubmissions();
                    Iterator<Submission> submissionIterator = submissions.iterator();
                    while(submissionIterator.hasNext()) {
                        Submission s = submissionIterator.next();
                        if(!s.getUser().getEmail().equals(email)) {
                            submissionIterator.remove();
                        }
                    }
                }
            }
            return new CourseAssignmentResponseDTO(assignmentList,"Successfully fetched all Assignments",true);
        }
        catch (Exception e) {
            return new CourseAssignmentResponseDTO(null,e.getMessage(),false);
        }

    }
    @Override
    public CourseAnnouncementResponseDTO getAnnouncement(Integer courseId) {
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to fetch Course with ID " + courseId));
            List<Announcement> announcementList = course.getAnnouncements();
            return new CourseAnnouncementResponseDTO(announcementList, "Successfully fetched all Announcements",true);
        }
        catch (Exception e) {
            return new CourseAnnouncementResponseDTO(null, e.getMessage(),false);
        }
    }

    @Override
    public CourseDTO getCourseDetails(Integer courseId) {
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to fetch Course with ID " + courseId));
            CourseDTO courseDTO = this.modelMapper.map(course,CourseDTO.class);
            return courseDTO;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public CourseResponseDTO archieveCourse(Integer courseId, String email, String role) {
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to Find Course With ID " + courseId));
            User user = this.userRepository.findByEmail(email);
            if(user.getArchievedCourses().contains(course)) {
                return new CourseResponseDTO("Course Already Archieved",false);
            }
            user.getArchievedCourses().add(course);
            course.getArchivedUsers().add(user);
            this.userRepository.save(user);
            return new CourseResponseDTO("Archieved Course",true);
        }
        catch (Exception e) {
            System.out.println(e);
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public List<CourseDTO> getArchievedCourses(String email) {
        try {
            User user = this.userRepository.findByEmail(email);
            List<Course> archievedCourses = user.getArchievedCourses();
            List<CourseDTO> archievedCourseDTOs = archievedCourses.stream().map(archievedCourse -> this.modelMapper.map(archievedCourse,CourseDTO.class)).collect(Collectors.toList());
            return archievedCourseDTOs;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public CourseResponseDTO unarchieveCourse(Integer courseId, String email) {
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable To Find Course With ID " + courseId));
            User user = this.userRepository.findByEmail(email);
            if(user.getArchievedCourses().contains(course)) {
                user.getArchievedCourses().remove(course);
                course.getArchivedUsers().remove(user);
                this.userRepository.save(user);
                this.courseRepository.save(course);
                return new CourseResponseDTO("Course UnArchieved Successfully",true);
            }
            else {
                return new CourseResponseDTO("Course is already UnArchieved",false);
            }
        }
        catch (Exception e) {
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO, String email, String role) {
        User user = this.userRepository.findByEmail(email);
        if(role.equals("INSTRUCTOR")) {
            Course course = new Course();
            String courseCode = generateUniqueCode();
            course = this.modelMapper.map(courseDTO,Course.class);
            course.setInstructor(user);
            course.setCode(courseCode);
            user.getCreatedCourses().add(course);
            this.userRepository.save(user);
            this.courseRepository.save(course);
            return this.modelMapper.map(course,CourseDTO.class);
        }
        else {
            return null;
        }
    }

    @Override
    public CourseResponseDTO createAnnouncement(String email, String role, Integer courseId, List<MultipartFile> files, String name, String content) {
        User user = this.userRepository.findByEmail(email);
        if(role.equals("STUDENT")) {
            return new CourseResponseDTO("You must be a Instructor in order to create an Assignment",true);
        }
        try {
            Course course = this.courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Unable to fetch Course With ID " + courseId));
            if(course.getInstructor().equals(user)) {
                List<File> uploadfiles = new ArrayList<>();
                if(files != null && files.size() > 0) {
                    for(MultipartFile file : files) {
                        String fileName = file.getOriginalFilename();
                        Map data = this.cloudinary.uploader().upload(file.getBytes(),Map.of());
                        String uploadedLink = (String) data.get("secure_url");
                        File assignmentFile = new File();
                        assignmentFile.setFileName(fileName);
                        assignmentFile.setFilePath(uploadedLink);
                        File savedFile = this.fileRepository.save(assignmentFile);
                        uploadfiles.add(savedFile);
                    }
                }
                Announcement announcement = new Announcement();
                announcement.setName(name);
                announcement.setContent(content);
                announcement.setCourse(course);
                announcement.setFiles(uploadfiles);
                Announcement savedAnnouncement = this.announcementRepository.save(announcement);
                course.getAnnouncements().add(savedAnnouncement);
                this.courseRepository.save(course);
                String htmlContent = """
                                        <html>
                                        <head>
                                            <style>
                                                .card {
                                                    background-color: #f9f9f9;
                                                    border: 1px solid #ddd;
                                                    border-radius: 8px;
                                                    padding: 20px;
                                                    width: 300px;
                                                    margin: 20px auto;
                                                }
                                                .fade-background {
                                                    background: linear-gradient(rgba(255,255,255,0), rgba(255,255,255,1));
                                                    padding: 10px;
                                                    border-radius: 8px;
                                                }
                                                .button {
                                                    display: inline-block;
                                                    padding: 10px 20px;
                                                    font-size: 16px;
                                                    text-align: center;
                                                    text-decoration: none;
                                                    background-color: #007BFF;
                                                    color: #fff;
                                                    border-radius: 5px;
                                                    cursor: pointer;
                                                }
                                            </style>
                                        </head>
                                        <body>
                                            <div class="card">
                                                <div class="fade-background">
                                                    <h2>%s</h2>
                                                    <p>Course: %s</p>
                                                    <a href="http://localhost:3000/announcement" class="button">Click to View</a>
                                                </div>
                                            </div>
                                        </body>
                                        </html>
                                        """.formatted(name, course.getCourseName());
                for(User student : course.getEnrolledUsers()) {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                    helper.setFrom(sender);
                    helper.setTo(student.getEmail());
                    helper.setText(htmlContent, true);
                    helper.setSubject("New Announcement for " + course.getCourseName());
                    javaMailSender.send(mimeMessage);
                }
                return new CourseResponseDTO("Successfully Added Announcement to Course",true);
            }
            else {
                return new CourseResponseDTO("You are not an Instructor for this Course",false);
            }
        }
        catch (Exception e) {
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }
}
