package com.Hindol.Classroom.Entity;

import com.Hindol.Classroom.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String password;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpires;
    @OneToMany(mappedBy = "instructor")
    @JsonIgnore
    private List<Course> createdCourses;

    @ManyToMany(mappedBy = "enrolledUsers")
    @JsonIgnore
    private List<Course> enrolledCourses;

    @ManyToMany(mappedBy = "enrolledUsers")
    @JsonIgnore
    private List<Course> archievedCourses;

    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Submission> submissions;
}
