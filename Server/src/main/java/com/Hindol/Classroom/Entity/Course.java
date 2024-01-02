package com.Hindol.Classroom.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User instructor;

    @ManyToMany
    @JoinTable(
            name = "enrolled_users_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> enrolledUsers;


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Announcement> announcements;

    private String courseName;
    private String code;
}
