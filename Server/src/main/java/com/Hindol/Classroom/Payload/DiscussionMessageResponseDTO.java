package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscussionMessageResponseDTO {
    private String message;
    private Boolean success;
    private List<Message> discussionMessageList;
}
