package com.taurustex.api.models;

import com.taurustex.api.basis.BaseEntity;
import com.taurustex.api.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Setter @Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Payment extends BaseEntity {

    private String numberOfTransactions;
    private BigDecimal amount;
    private String transferPhone;
    private LocalTime transferHour;
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    @OneToOne
    private Candidate candidate;

}
