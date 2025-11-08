package com.mertcanengin.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grade_components",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lecture_id", "name"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class GradeComponent extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double weight;

    @Column(name = "max_score", nullable = false)
    private Double maxScore = 100d;

    @JsonIgnore
    @OneToMany(mappedBy = "gradeComponent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnrollmentGrade> enrollmentGrades = new ArrayList<>();
}
