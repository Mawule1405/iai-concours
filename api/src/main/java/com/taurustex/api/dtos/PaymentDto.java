package com.taurustex.api.dtos;

import com.taurustex.api.basis.BaseEntity;
import com.taurustex.api.enums.PaymentMethod;
import com.taurustex.api.models.Candidate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@Setter @Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentDto  {

    private String id;
    private String numberOfTransactions;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String transferPhone;
    private LocalTime transferHour;

    private PaymentMethod paymentMethod;

    private String candidateId;
    private String candidateName;


}
