package com.Hindol.Classroom.Payload;

import com.Hindol.Classroom.Entity.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private Role role;
    private String email;
    private String password;
}
