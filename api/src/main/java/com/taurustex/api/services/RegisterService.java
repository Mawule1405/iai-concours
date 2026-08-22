package com.taurustex.api.services;

import com.taurustex.api.dtos.CandidateDto;
import com.taurustex.api.dtos.PaymentDto;
import org.springframework.transaction.annotation.Transactional;

public interface RegisterService {
    @Transactional
    CandidateDto registerCandidate(CandidateDto candidateDto);

    @Transactional
    PaymentDto registerPayment(String candidateId, PaymentDto paymentDto);

    CandidateDto getCandidate(String numero);
}
