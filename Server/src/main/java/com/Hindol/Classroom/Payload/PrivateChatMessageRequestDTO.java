package com.Hindol.Classroom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChatMessageRequestDTO {
    private String content;
    private String Type;
}
