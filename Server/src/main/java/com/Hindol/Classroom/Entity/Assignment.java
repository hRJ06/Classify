package com.Hindol.Classroom.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonIgnore
    private Course course;
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_id")
    private List<File> file;

    @OneToMany(mappedBy = "assignment")
    private List<Submission> submissions;
    private String assignmentName;
    private LocalDateTime deadline;
    private Integer fullMarks;
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<PrivateChat> privateChats;
}
