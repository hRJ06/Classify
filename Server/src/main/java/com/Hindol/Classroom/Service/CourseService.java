package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.*;
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
    CourseResponseDTO archieveCourse(Integer courseId,String email,String role);
    List<CourseDTO> getArchievedCourses(String email);
    CourseResponseDTO unarchieveCourse(Integer courseId,String email);
    CourseResponseDTO addDiscussionMessage(Integer courseId,DiscussionMessageRequestDTO discussionMessageRequestDTO,String email,String role);
    DiscussionMessageResponseDTO getDiscussionMessage(Integer courseId,String email,String role);
    CourseResponseDTO addDoubt(Integer courseId,DoubtRequestDTO doubtRequestDTO,String email,String role);
    DoubtDTO getDoubt(Integer courseId);
    CourseResponseDTO addMeetLink(String email,String role,Integer courseId, String meetLink);
    CourseResponseDTO uploadCoverPhoto(String role,Integer courseId,MultipartFile file);
}
