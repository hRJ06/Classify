package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.MessageEditDTO;
import com.Hindol.Classroom.Payload.MessageResponseDTO;


public interface MessageService {
    MessageResponseDTO editMessage(Integer messageId, String email, MessageEditDTO messageEditDTO);
}
