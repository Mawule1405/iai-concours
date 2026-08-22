package com.taurustex.api.services.impl;

import com.taurustex.api.annotations.NotifyClients;
import com.taurustex.api.dtos.CandidateDto;
import com.taurustex.api.dtos.PaymentDto;
import com.taurustex.api.enums.Status;
import com.taurustex.api.exception.ResourceNotFoundException;
import com.taurustex.api.mappers.CandidateMapper;
import com.taurustex.api.mappers.PaymentMapper;
import com.taurustex.api.models.Candidate;
import com.taurustex.api.models.Payment;
import com.taurustex.api.repositories.CandidateRepository;
import com.taurustex.api.repositories.PaymentRepository;
import com.taurustex.api.services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final CandidateRepository candidateRepository;
    private final PaymentRepository paymentRepository;
    private final CandidateMapper candidateMapper;
    private final PaymentMapper paymentMapper;

    /**
     * Étape 1 : Enregistre le candidat et initialise son statut à REGISTERED_ONLY
     */
    @Transactional
    @Override
    @NotifyClients(topic = "/topic/candidates")
    public CandidateDto registerCandidate(CandidateDto candidateDto) {
        Candidate candidate = candidateMapper.toEntity(candidateDto);

        // Définition initiale du statut
        candidate.setStatus(Status.REGISTERED_ONLY);
        candidate.setNumero(generateNumero());

        Candidate savedCandidate = candidateRepository.save(candidate);
        return candidateMapper.toDto(savedCandidate);
    }

    /**
     * Étape 2 : Enregistre le paiement, lie le candidat et met à jour le statut global
     */
    @Transactional
    @Override
    @NotifyClients(topic = "/topic/candidates")
    public PaymentDto registerPayment(String candidateId, PaymentDto paymentDto) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidat non trouvé avec l'ID : " + candidateId));

        Payment payment = paymentMapper.toEntity(paymentDto);
        payment.setCandidate(candidate);


        Payment savedPayment = paymentRepository.save(payment);

        // Mise à jour de la référence de paiement et du statut dans le candidat
        candidate.setPayment(savedPayment);

        if (candidate.getStatus() == Status.REGISTERED_ONLY) {
            candidate.setStatus(Status.REGISTERED_AND_PAYMENT);
        } else {
            candidate.setStatus(Status.PAYMENT_ONLY);
        }

        candidateRepository.save(candidate);

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public CandidateDto getCandidate(String numero) {
        return candidateMapper.toDto(candidateRepository.findByNumero(numero).orElse(null));
    }


    private String generateNumero() {
        int currentYear = LocalDate.now().getYear();
        long nextSequence = candidateRepository.count() + 1; // +1 pour inclure le candidat en cours

        // %04d force l'affichage sur 4 chiffres avec bourrage de zéros à gauche (ex: 1 -> 0001)
        return String.format("%d%04d", currentYear, nextSequence);
    }

}