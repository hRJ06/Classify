package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.CourseAnnouncementResponseDTO;
import com.Hindol.Classroom.Payload.CourseAssignmentResponseDTO;
import com.Hindol.Classroom.Payload.CourseDTO;
import com.Hindol.Classroom.Payload.CourseResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO, String email, String role);
    List<CourseDTO> enrolledCourse(String email,String role);
    CourseResponseDTO enrollCourse(String email, String role, String courseId);
    CourseResponseDTO unrenrollCourse(String email, String role, Integer courseId);
    CourseResponseDTO createAssignment(String email, String role, Integer courseId, List<MultipartFile> files, String name, String description, String deadline,String fullMarks);
    CourseAssignmentResponseDTO getAssignment(Integer courseId,String email,String Role);
    CourseResponseDTO createAnnouncement(String email,String role,Integer courseId,List<MultipartFile> file,String name,String content);
    CourseAnnouncementResponseDTO getAnnouncement(Integer courseId);
    CourseDTO getCourseDetails(Integer courseId);
}
