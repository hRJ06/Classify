package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseAnnouncementResponseDTO {
    private List<Announcement> announcements;
    private String message;
    private Boolean success;
}
