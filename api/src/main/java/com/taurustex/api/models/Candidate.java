package com.taurustex.api.models;

import com.taurustex.api.basis.BaseEntity;
import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Candidate extends BaseEntity {

    private String numero;
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phone;
    private String tutorPhone;
    @Enumerated(EnumType.STRING)
    private Serie serie;
    private String nationality;
    private String numeroTable;
    @Enumerated(EnumType.STRING)
    private Option option;
    private LocalDate enrolmentDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @OneToOne
    private Payment payment;

}
