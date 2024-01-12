package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.Message;
import com.Hindol.Classroom.Entity.User;
import com.Hindol.Classroom.Payload.MessageEditDTO;
import com.Hindol.Classroom.Payload.MessageResponseDTO;
import com.Hindol.Classroom.Repository.MessageRepository;
import com.Hindol.Classroom.Repository.UserRepository;
import com.Hindol.Classroom.Service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageServiceImplementation implements MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public MessageResponseDTO editMessage(Integer messageId, String email, MessageEditDTO messageEditDTO) {
        try {
            Message message = this.messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Unable To Fetch Message By ID " + messageId));
            User user = this.userRepository.findByEmail(email);
            if(message.getSender().equals(user)) {
                message.setContent(messageEditDTO.getContent());
                this.messageRepository.save(message);
                return new MessageResponseDTO("Edited Message",true);
            }
            else {
                return new MessageResponseDTO("You Are Not Creator Of this Message",false);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return new MessageResponseDTO(e.getMessage(),false);
        }
    }
}
