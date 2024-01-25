package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.CourseResponseDTO;
import com.Hindol.Classroom.Payload.DoubtAnswerDTO;
import com.Hindol.Classroom.Payload.DoubtDTO;
import com.Hindol.Classroom.Payload.DoubtRequestDTO;
import com.Hindol.Classroom.Service.DoubtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/doubt")
public class DoubtController {
    @Autowired
    private DoubtService doubtService;
    @PostMapping("/add-answer/{doubtId}")
    public ResponseEntity<?> addAnswer(HttpServletRequest request, @PathVariable Integer doubtId, @RequestBody DoubtRequestDTO doubtRequestDTO) {
        String email = (String) request.getAttribute("email");
        CourseResponseDTO courseResponseDTO = this.doubtService.addAnswerToDoubt(doubtId,doubtRequestDTO,email);
        if(courseResponseDTO.getSuccess()) {
            return new ResponseEntity<CourseResponseDTO>(courseResponseDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CourseResponseDTO>(courseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/all-answer/{doubtId}")
    public ResponseEntity<?> getAnswer(HttpServletRequest request,@PathVariable Integer doubtId) {
        String email = (String) request.getAttribute("email");
        DoubtAnswerDTO doubtAnswerDTO = this.doubtService.getAnswer(email,doubtId);
        if(doubtAnswerDTO.getAnswerList() != null) {
            return new ResponseEntity<DoubtAnswerDTO>(doubtAnswerDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<DoubtAnswerDTO>(doubtAnswerDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/generate-answer/{doubtId}")
    public ResponseEntity<?> generateAnswer(HttpServletRequest request,@PathVariable Integer doubtId) {
        String email = (String) request.getAttribute("email");
        String AIAnswer = this.doubtService.generateAnswer(email,doubtId);
        if(AIAnswer != null) {
            return ResponseEntity.ok().body(Map.of("answer", AIAnswer));
        }
        else {
            return ResponseEntity.badRequest().body("Internal Server Error");
        }
    }
    @GetMapping("/get-doubt/{courseId}")
    public ResponseEntity<?> getDoubt(@RequestParam(name = "search", required = false) String keyword,@PathVariable Integer courseId,HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        DoubtDTO doubtDTO = this.doubtService.searchDoubts(courseId,keyword);
        if(doubtDTO.getDoubtList() != null) {
            return new ResponseEntity<DoubtDTO>(doubtDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<DoubtDTO>(doubtDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/doubts")
    public ResponseEntity<?> getMyDoubts(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        DoubtDTO doubtDTO = this.doubtService.getMyDoubts(email);
        if(doubtDTO.getDoubtList() != null) {
            return new ResponseEntity<DoubtDTO>(doubtDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<DoubtDTO>(doubtDTO,HttpStatus.BAD_REQUEST);
        }
    }
}
