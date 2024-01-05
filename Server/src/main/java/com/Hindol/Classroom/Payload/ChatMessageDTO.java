package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Message;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String message;
    private Boolean success;
    private List<Message> messageList;
}
