package com.taurustex.api.services;

import com.taurustex.api.dtos.PaymentDto;

public interface PaymentService {
    PaymentDto updatePayment(String paymentId, PaymentDto paymentDto);

    PaymentDto getPayment(String paymentId);

    boolean deletePayment(String paymentId);
}
