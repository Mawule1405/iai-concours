package com.taurustex.api.dtos;

import com.taurustex.api.enums.Option;
import com.taurustex.api.enums.PaymentMethod;
import com.taurustex.api.enums.Serie;
import com.taurustex.api.enums.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CandidateExportDto {

    private String numero;
    private String lastName;
    private String firstName;
    private String birthDate;
    private String gender;
    private String email;
    private String phone;
    private String serie;
    private String nationality;
    private String option;

    private String numberOfTransactions;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String transferPhone;
    private LocalTime transferHour;

    private String numeroTable;

}
