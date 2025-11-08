package com.mertcanengin.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.common.AuditableEntity;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lecture_id", "student_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Enrollment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column
    private Double grade;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();

    public Integer getLectureId() {
        return lecture != null ? lecture.getId() : null;
    }

    public Integer getStudentId() {
        return student != null ? student.getId() : null;
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = EnrollmentStatus.ACTIVE;
        }
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
    }
}
