package com.taurustex.api.controllers;

import com.taurustex.api.dtos.CandidateDto;
import com.taurustex.api.dtos.PaymentDto;
import com.taurustex.api.services.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iai-concours-api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    /**
     * Étape 1 : Enregistrement des informations du candidat
     * POST /api/v1/register/candidate
     */
    @PostMapping("/candidate")
    //@PreAuthorize("hasAuthority('PERM_REGISTER_CANDIDATE')")
    public ResponseEntity<CandidateDto> registerCandidate(@Valid @RequestBody CandidateDto candidateDto) {
        CandidateDto createdCandidate = registerService.registerCandidate(candidateDto);
        return new ResponseEntity<>(createdCandidate, HttpStatus.CREATED);
    }

    @GetMapping("/candidate/{numero}")
    //@PreAuthorize("hasAuthority('PERM_REGISTER_CANDIDATE')")
    public ResponseEntity<CandidateDto> registerCandidate(@PathVariable String numero) {
        CandidateDto createdCandidate = registerService.getCandidate(numero);
        return ResponseEntity.ok(createdCandidate);
    }

    /**
     * Étape 2 : Enregistrement du paiement associé au candidat
     * POST /api/v1/register/candidates/{candidateId}/payment
     */
    @PostMapping("/candidates/{candidateId}/payment")
    //@PreAuthorize("hasAuthority('PERM_REGISTER_PAYMENT_CANDIDATE')")
    public ResponseEntity<PaymentDto> registerPayment(
            @PathVariable String candidateId,
            @Valid @RequestBody PaymentDto paymentDto) {
        PaymentDto createdPayment = registerService.registerPayment(candidateId, paymentDto);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }
}