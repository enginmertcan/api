package com.mertcanengin.api.entity;

import com.mertcanengin.api.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "enrollment_grades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "grade_component_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class EnrollmentGrade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grade_component_id")
    private GradeComponent gradeComponent;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;
}
