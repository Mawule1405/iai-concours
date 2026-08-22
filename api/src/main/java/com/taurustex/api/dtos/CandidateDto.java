package com.taurustex.api.dtos;

import com.taurustex.api.basis.BaseEntity;
import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CandidateDto  {

    private String id;
    private String numero;
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String tutorPhone;
    private String phone;
    private Serie serie;
    private String nationality;
    private String numeroTable;
    private Option option;
    private LocalDate enrolmentDate;

    private Status status = Status.PENDING;
    private String paymentId;

}
