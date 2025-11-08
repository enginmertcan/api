package com.mertcanengin.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "lectures")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column
    private String name;

    @Column
    private Integer id;

    public  Integer getTeacherId() {
        return teacher.getId();
    }


    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToMany
    @JoinTable(
            name = "user_lectures",
            joinColumns = @JoinColumn(name = "lecture_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List <User> students;
}
