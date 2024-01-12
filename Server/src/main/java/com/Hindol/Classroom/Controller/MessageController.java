package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.MessageEditDTO;
import com.Hindol.Classroom.Payload.MessageResponseDTO;
import com.Hindol.Classroom.Service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @PutMapping("/edit-message/{messageId}")
    public ResponseEntity<?> editMessage(@PathVariable Integer messageId, HttpServletRequest request, MessageEditDTO messageEditDTO) {
        String email = (String) request.getAttribute("email");
        MessageResponseDTO messageResponseDTO = this.messageService.editMessage(messageId,email,messageEditDTO);
        if(messageResponseDTO.getSuccess()) {
            return new ResponseEntity<MessageResponseDTO>(messageResponseDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<MessageResponseDTO>(messageResponseDTO,HttpStatus.BAD_REQUEST);
        }
    }
}
