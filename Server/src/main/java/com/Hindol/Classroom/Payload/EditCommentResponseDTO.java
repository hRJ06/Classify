package com.Hindol.Classroom.Payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditCommentResponseDTO {
    private String message;
    private Boolean success;
}
