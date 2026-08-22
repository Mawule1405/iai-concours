package com.taurustex.api.services.impl;

import com.taurustex.api.annotations.NotifyClients;
import com.taurustex.api.dtos.PaymentDto;
import com.taurustex.api.exception.ResourceNotFoundException;
import com.taurustex.api.mappers.PaymentMapper;
import com.taurustex.api.models.Payment;
import com.taurustex.api.repositories.PaymentRepository;
import com.taurustex.api.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @NotifyClients(topic = "/topic/candidates")
    public PaymentDto updatePayment(String paymentId, PaymentDto paymentDto) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }

        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setTransferHour(paymentDto.getTransferHour());
        payment.setTransferPhone(paymentDto.getTransferPhone());
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setNumberOfTransactions(paymentDto.getNumberOfTransactions());
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto getPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        return paymentMapper.toDto(payment);
    }

    @Override
    @NotifyClients(topic = "/topic/candidates")
    public boolean deletePayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found");
        }
        paymentRepository.delete(payment);
        return true;
    }


}
