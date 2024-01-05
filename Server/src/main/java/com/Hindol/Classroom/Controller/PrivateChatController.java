package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.ChatMessageDTO;
import com.Hindol.Classroom.Payload.PrivateChatMessageRequestDTO;
import com.Hindol.Classroom.Payload.PrivateChatResponseDTO;
import com.Hindol.Classroom.Service.PrivateChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
public class PrivateChatController {
    @Autowired
    private PrivateChatService privateChatService;
    @PostMapping("/add-message/{privateChatId}")
    public ResponseEntity<?> addMessage(HttpServletRequest request, @PathVariable Integer privateChatId, @RequestBody PrivateChatMessageRequestDTO privateChatMessageRequestDTO) {
        String email = (String) request.getAttribute("email");
        String Role = (String) request.getAttribute("Role");
        PrivateChatResponseDTO privateChatResponseDTO = this.privateChatService.addMessage(email,Role,privateChatId,privateChatMessageRequestDTO);
        if(privateChatResponseDTO.getSuccess()) {
            return new ResponseEntity<PrivateChatResponseDTO>(privateChatResponseDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<PrivateChatResponseDTO>(privateChatResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/get-message/{privateChatId}")
    public ResponseEntity<?> getMessages(HttpServletRequest request,@PathVariable Integer privateChatId) {
        String email = (String) request.getAttribute("email");
        String Role = (String) request.getAttribute("Role");
        ChatMessageDTO chatMessageDTO = this.privateChatService.getMessages(email,Role,privateChatId);
        if(chatMessageDTO.getSuccess()) {
            return new ResponseEntity<ChatMessageDTO>(chatMessageDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<ChatMessageDTO>(chatMessageDTO,HttpStatus.BAD_REQUEST);
        }
    }
}
