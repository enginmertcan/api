package com.mertcanengin.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mertcanengin.api.entity.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "enrollment_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "week_of"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class EnrollmentAttendance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "week_of", nullable = false)
    private LocalDate weekOf;

    @Column(name = "attended", nullable = false)
    private boolean attended = true;
}
