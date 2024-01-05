package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.CourseAnnouncementResponseDTO;
import com.Hindol.Classroom.Payload.CourseAssignmentResponseDTO;
import com.Hindol.Classroom.Payload.CourseDTO;
import com.Hindol.Classroom.Payload.CourseResponseDTO;
import com.Hindol.Classroom.Service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course")
@CrossOrigin("*")
public class CourseController {
    @Autowired
    private CourseService  courseService;
    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        CourseDTO savedcourseDTO = this.courseService.createCourse(courseDTO,email,role);
        if(savedcourseDTO != null) {
            return new ResponseEntity<CourseDTO>(savedcourseDTO, HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getCourses")
    public ResponseEntity<List<CourseDTO>> getCourses(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        List<CourseDTO> courseDTOList = this.courseService.enrolledCourse(email,role);
        return new ResponseEntity<List<CourseDTO>>(courseDTOList,HttpStatus.OK);
    }
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseDetails(@PathVariable Integer courseId) {
        CourseDTO courseDTO = this.courseService.getCourseDetails(courseId);
        if(courseDTO != null) {
            return new ResponseEntity<CourseDTO>(courseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseDTO>(courseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/enroll/{courseCode}")
    public ResponseEntity<?> enrollCourse(HttpServletRequest request,@PathVariable String courseCode) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        CourseResponseDTO enrollCourseResponseDTO = this.courseService.enrollCourse(email,role,courseCode);
        if(enrollCourseResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseResponseDTO>(enrollCourseResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseResponseDTO>(enrollCourseResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/unenroll/{courseId}")
    public ResponseEntity<?> unenrollCourse(HttpServletRequest request,@PathVariable Integer courseId) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        CourseResponseDTO enrollCourseResponseDTO = this.courseService.unrenrollCourse(email,role,courseId);
        if(enrollCourseResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseResponseDTO>(enrollCourseResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseResponseDTO>(enrollCourseResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/createAssignment/{courseId}")
    public ResponseEntity<?> createAssignment(@RequestParam(value = "files", required = false) List<MultipartFile> files,@RequestParam("fullMarks") String fullMarks,@RequestParam("name") String assignmentName,@RequestParam("description") String assignmentDescription, @RequestParam("deadline") String deadline,@PathVariable Integer courseId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        System.out.println("Files " + files);
        CourseResponseDTO courseResponseDTO = this.courseService.createAssignment(email,role,courseId,files,assignmentName,assignmentDescription,deadline,fullMarks);
        System.out.println("COURSE RESPONSE" + courseResponseDTO.getResult());
        if(courseResponseDTO.getSuccess()) {
            return new ResponseEntity<>(courseResponseDTO,HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<>(courseResponseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getAssignment/{courseId}")
    public ResponseEntity<CourseAssignmentResponseDTO> getAssignment(@PathVariable Integer courseId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String Role = (String) request.getAttribute("Role");
        CourseAssignmentResponseDTO courseAssignmentResponseDTO = this.courseService.getAssignment(courseId,email,Role);
        if(courseAssignmentResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseAssignmentResponseDTO>(courseAssignmentResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseAssignmentResponseDTO>(courseAssignmentResponseDTO,HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/createAnnouncement/{courseId}")
    public ResponseEntity<?> createAnnouncement(@RequestParam(value = "files", required = false) List<MultipartFile> files,@RequestParam("name") String name,@RequestParam("content") String content,@PathVariable Integer courseId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("Role");
        CourseResponseDTO courseResponseDTO = this.courseService.createAnnouncement(email,role,courseId,files,name,content);
        System.out.println(courseResponseDTO.getResult());
        if(courseResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseResponseDTO>(courseResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseResponseDTO>(courseResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAnnouncement/{courseId}")
    public ResponseEntity<?> getAnnouncement(@PathVariable Integer courseId,HttpServletRequest request) {
        CourseAnnouncementResponseDTO courseAnnouncementResponseDTO = this.courseService.getAnnouncement(courseId);
        if(courseAnnouncementResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseAnnouncementResponseDTO>(courseAnnouncementResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(courseAnnouncementResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/archieve/{courseId}")
    public ResponseEntity<?> archieveCourse(@PathVariable Integer courseId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String Role = (String) request.getAttribute("Role");
        CourseResponseDTO courseResponseDTO = this.courseService.archieveCourse(courseId,email,Role);
        if(courseResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseResponseDTO>(courseResponseDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseResponseDTO>(courseResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/unarchieve/{courseId}")
    public ResponseEntity<?> unarchieveCourse(@PathVariable Integer courseId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
    }
    @GetMapping("/get-archieved-courses")
    public ResponseEntity<?> getArchievedCourses(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        List<CourseDTO> courseDTOS = this.courseService.getArchievedCourses(email);
        if(courseDTOS != null) {
            return new ResponseEntity<List<CourseDTO>>(courseDTOS,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<List<CourseDTO>>(courseDTOS,HttpStatus.BAD_REQUEST);
        }
    }
}

