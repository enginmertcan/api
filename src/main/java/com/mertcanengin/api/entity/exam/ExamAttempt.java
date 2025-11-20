package com.mertcanengin.api.entity.exam;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.common.AuditableEntity;
import com.mertcanengin.api.entity.enums.ExamAttemptStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exam_attempts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamAttempt extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamAttemptStatus status = ExamAttemptStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(precision = 6, scale = 2)
    private BigDecimal score;

    @JsonIgnore
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamAnswer> answers = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ExamAttemptStatus.IN_PROGRESS;
        }
    }

    public void markSubmitted(LocalDateTime submissionTime, BigDecimal totalScore) {
        submittedAt = submissionTime;
        status = ExamAttemptStatus.SUBMITTED;
        score = totalScore;
        if (startedAt != null && submissionTime != null) {
            durationSeconds = (int) Duration.between(startedAt, submissionTime).getSeconds();
        }
    }
}

