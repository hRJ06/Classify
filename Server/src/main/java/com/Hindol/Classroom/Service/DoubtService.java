package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.CourseResponseDTO;
import com.Hindol.Classroom.Payload.DoubtAnswerDTO;
import com.Hindol.Classroom.Payload.DoubtDTO;
import com.Hindol.Classroom.Payload.DoubtRequestDTO;

public interface DoubtService {
    CourseResponseDTO addAnswerToDoubt(Integer doubtId, DoubtRequestDTO doubtRequestDTO,String email);
    DoubtAnswerDTO getAnswer(String email,Integer doubtId);
    String generateAnswer(String email,Integer doubtId);
    DoubtDTO searchDoubts(Integer courseId, String keyword);
}
