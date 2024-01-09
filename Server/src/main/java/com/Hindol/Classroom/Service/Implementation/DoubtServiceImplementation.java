package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.Doubt;
import com.Hindol.Classroom.Entity.Message;
import com.Hindol.Classroom.Entity.User;
import com.Hindol.Classroom.Payload.CourseResponseDTO;
import com.Hindol.Classroom.Payload.DoubtAnswerDTO;
import com.Hindol.Classroom.Payload.DoubtRequestDTO;
import com.Hindol.Classroom.Repository.DoubtRepository;
import com.Hindol.Classroom.Repository.MessageRepository;
import com.Hindol.Classroom.Repository.UserRepository;
import com.Hindol.Classroom.Service.DoubtService;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class DoubtServiceImplementation implements DoubtService {
    @Autowired
    private DoubtRepository doubtRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public CourseResponseDTO addAnswerToDoubt(Integer doubtId, DoubtRequestDTO doubtRequestDTO, String email) {
        try {
            Doubt doubt = this.doubtRepository.findById(doubtId).orElseThrow(() -> new RuntimeException("Unable To Fetch Doubt With ID " + doubtId));
            User user = this.userRepository.findByEmail(email);
            Message message = new Message();
            message.setContent(doubtRequestDTO.getContent());
            message.setSender(user);
            message.setDoubt(doubt);
            Message savedMessage = this.messageRepository.save(message);
            doubt.getAnswers().add(savedMessage);
            this.doubtRepository.save(doubt);
            return new CourseResponseDTO("Successfully Added Answer To Doubt",true);
        }
        catch (Exception e) {
            return new CourseResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public DoubtAnswerDTO getAnswer(String email,Integer doubtId) {
        try {
            Doubt doubt = this.doubtRepository.findById(doubtId).orElseThrow(() -> new RuntimeException("Unable to fetch Doubt with ID " + doubtId));
            User user = this.userRepository.findByEmail(email);
            if(doubt.getCourse().getInstructor().equals(user) || doubt.getCourse().getEnrolledUsers().contains(user)) {
                List<Message> answerList = doubt.getAnswers();
                return new DoubtAnswerDTO(answerList);
            }
            else {
                return new DoubtAnswerDTO(null);
            }
        }
        catch (Exception e) {
            return new DoubtAnswerDTO(null);
        }
    }

    @Override
    public String generateAnswer(String email, Integer doubtId) {
        try {
            Doubt doubt = this.doubtRepository.findById(doubtId).orElseThrow(() -> new RuntimeException("Unable to fetch Doubt with ID " + doubtId));
            User user = this.userRepository.findByEmail(email);
            if(doubt.getCourse().getInstructor().equals(user) || doubt.getCourse().getEnrolledUsers().contains(user)) {
                String apiUrl = "https://simple-chatgpt-api.p.rapidapi.com/ask";
                String apiKey = "7adf0ccd29msh67ae8f41e7e0a05p1268a3jsn08dcb39bfdca";
                String requestBody = "{\"question\": \"" + doubt.getContent() + "\"}";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .header("X-RapidAPI-Key", apiKey)
                        .header("X-RapidAPI-Host", "simple-chatgpt-api.p.rapidapi.com")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonObject = new JSONObject(response.body());
                return jsonObject.getString("answer");
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }
}
