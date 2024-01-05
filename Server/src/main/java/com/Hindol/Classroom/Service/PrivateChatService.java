package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Entity.Message;
import com.Hindol.Classroom.Payload.ChatMessageDTO;
import com.Hindol.Classroom.Payload.PrivateChatMessageRequestDTO;
import com.Hindol.Classroom.Payload.PrivateChatResponseDTO;

import java.util.List;

public interface PrivateChatService {
    PrivateChatResponseDTO addMessage(String email, String Role, Integer privateChatId, PrivateChatMessageRequestDTO privateChatMessageRequestDTO);
    ChatMessageDTO getMessages(String email, String Role, Integer privateChatId);
}
