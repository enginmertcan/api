package com.mertcanengin.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mertcanengin.api.entity.common.AuditableEntity;
import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(name = "identity_no", length =11 , unique = true)
    private String identityNo;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "urole")
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
}
