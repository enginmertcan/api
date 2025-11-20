package com.mertcanengin.api.entity.exam;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.common.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exam_answers")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamAnswer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private ExamQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private ExamQuestionOption selectedOption;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "is_correct")
    private Boolean correct;

    @Column(name = "score_awarded", precision = 6, scale = 2)
    private BigDecimal scoreAwarded;

    public void linkAttempt(ExamAttempt targetAttempt) {
        attempt = targetAttempt;
        if (!targetAttempt.getAnswers().contains(this)) {
            targetAttempt.getAnswers().add(this);
        }
    }
}

