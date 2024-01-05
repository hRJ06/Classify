package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.Message;
import com.Hindol.Classroom.Entity.PrivateChat;
import com.Hindol.Classroom.Entity.User;
import com.Hindol.Classroom.Payload.ChatMessageDTO;
import com.Hindol.Classroom.Payload.PrivateChatMessageRequestDTO;
import com.Hindol.Classroom.Payload.PrivateChatResponseDTO;
import com.Hindol.Classroom.Repository.MessageRepository;
import com.Hindol.Classroom.Repository.PrivateChatRepository;
import com.Hindol.Classroom.Repository.UserRepository;
import com.Hindol.Classroom.Service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateChatServiceImplementation implements PrivateChatService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PrivateChatRepository privateChatRepository;
    @Override
    public PrivateChatResponseDTO addMessage(String email, String Role, Integer privateChatId, PrivateChatMessageRequestDTO privateChatMessageRequestDTO) {
        try {
            User user = this.userRepository.findByEmail(email);
            PrivateChat privateChat = this.privateChatRepository.findById(privateChatId).orElseThrow(() -> new RuntimeException("Unable to Fetch Private Chat with ID " + privateChatId));
            Message message = new Message();
            message.setContent(privateChatMessageRequestDTO.getContent());
            message.setSender(user);
            message.setChat(privateChat);
            Message savedMessage = this.messageRepository.save(message);
            privateChat.getMessageList().add(message);
            PrivateChat savedPrivateChat = this.privateChatRepository.save(privateChat);
            return new PrivateChatResponseDTO("Successfully Added Messaege to Chat",true);

        }
        catch (Exception e) {
            return new PrivateChatResponseDTO(e.getMessage(),false);
        }
    }

    @Override
    public ChatMessageDTO getMessages(String email, String Role, Integer privateChatId) {
        try {
            User user = this.userRepository.findByEmail(email);
            PrivateChat privateChat = this.privateChatRepository.findById(privateChatId).orElseThrow(() -> new RuntimeException("Unable to fetch Private Chat With ID " + privateChatId));
            if(!privateChat.getUser().equals(user) && !privateChat.getAssignment().getCourse().getInstructor().equals(user)) {
                return new ChatMessageDTO("You are Not part of this Chat",false,null);
            }
            List<Message> messageList = privateChat.getMessageList();
            return new ChatMessageDTO("Successfully fetched All Messages",true,messageList);
        }
        catch (Exception e) {
            System.out.println(e);
            return new ChatMessageDTO(e.getMessage(),false,null);
        }
    }

}
