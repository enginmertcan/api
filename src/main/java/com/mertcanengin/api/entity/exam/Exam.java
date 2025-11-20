package com.mertcanengin.api.entity.exam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.common.AuditableEntity;
import com.mertcanengin.api.entity.enums.ExamStatus;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exams")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Exam extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status = ExamStatus.DRAFT;

    @Column(name = "opens_at", nullable = false)
    private LocalDateTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalDateTime closesAt;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "total_score", precision = 6, scale = 2, nullable = false)
    private BigDecimal totalScore = BigDecimal.ZERO;

    @JsonIgnore
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestion> questions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamAttempt> attempts = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = ExamStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (status == null) {
            status = ExamStatus.DRAFT;
        }
    }

    public void addQuestion(ExamQuestion question) {
        if (question == null) {
            return;
        }
        question.setExam(this);
        this.questions.add(question);
    }

    public boolean isWindowOpen(LocalDateTime now) {
        if (opensAt == null || closesAt == null || now == null) {
            return false;
        }
        return !now.isBefore(opensAt) && now.isBefore(closesAt);
    }

    public void recalculateStatus(LocalDateTime now) {
        if (now == null || opensAt == null || closesAt == null) {
            return;
        }
        if (now.isBefore(opensAt)) {
            status = ExamStatus.SCHEDULED;
        } else if (!now.isAfter(closesAt)) {
            status = ExamStatus.ACTIVE;
        } else {
            status = ExamStatus.CLOSED;
        }
    }
}

