package com.mertcanengin.api.entity.exam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.common.AuditableEntity;
import com.mertcanengin.api.entity.enums.ExamQuestionType;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exam_questions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamQuestion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private ExamQuestionType questionType;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "points", precision = 6, scale = 2, nullable = false)
    private BigDecimal points = BigDecimal.ONE;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestionOption> options = new ArrayList<>();

    public void addOption(ExamQuestionOption option) {
        if (option == null) {
            return;
        }
        option.setQuestion(this);
        options.add(option);
    }
}
