package com.mertcanengin.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.common.AuditableEntity;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    private EnrollmentStatus status = EnrollmentStatus.PENDING_APPROVAL;

    @Column(name = "grade", precision = 5, scale = 2)
    private BigDecimal finalGrade;

    @Column(name = "waitlist_position")
    private Integer waitlistPosition;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "passed", nullable = false)
    private boolean passed = false;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnrollmentGrade> grades = new ArrayList<>();

    @Column(name = "absence_count", nullable = false)
    private Integer absenceCount = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnrollmentAttendance> attendanceRecords = new ArrayList<>();

    public Integer getLectureId() {
        return lecture != null ? lecture.getId() : null;
    }

    public Integer getStudentId() {
        return student != null ? student.getId() : null;
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = EnrollmentStatus.PENDING_APPROVAL;
        }
        if (enrolledAt == null) {
            enrolledAt = LocalDateTime.now();
        }
        if (approvedAt == null && status == EnrollmentStatus.ACTIVE) {
            approvedAt = LocalDateTime.now();
        }
    }
}
