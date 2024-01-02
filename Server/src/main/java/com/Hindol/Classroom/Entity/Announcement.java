package com.Hindol.Classroom.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String content;
    @ManyToOne
    @JsonIgnore
    private Course course;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "announcement_id")
    private List<File> files;
}
