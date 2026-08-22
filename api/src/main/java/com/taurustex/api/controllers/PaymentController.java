package com.taurustex.api.controllers;

import com.taurustex.api.dtos.PaymentDto;
import com.taurustex.api.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iai-concours-api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Récupérer les détails d'un paiement par son identifiant
     *
     * @param paymentId Identifiant unique du paiement
     * @return PaymentDto avec le statut HTTP 200 OK
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable String paymentId) {
        PaymentDto payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Mettre à jour les informations d'un paiement
     *
     * @param paymentId Identifiant unique du paiement à modifier
     * @param paymentDto Les nouvelles données du paiement
     * @return PaymentDto mis à jour avec le statut HTTP 200 OK
     */
    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> updatePayment(
            @PathVariable String paymentId,
            @Valid @RequestBody PaymentDto paymentDto) {

        PaymentDto updatedPayment = paymentService.updatePayment(paymentId, paymentDto);
        return ResponseEntity.ok(updatedPayment);
    }

    /**
     * Supprimer un paiement par son identifiant
     *
     * @param paymentId Identifiant unique du paiement à supprimer
     * @return HTTP 200 OK si supprimé, HTTP 404 NOT FOUND si inexistant
     */
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable String paymentId) {
        boolean deleted = paymentService.deletePayment(paymentId);

        if (deleted) {
            return ResponseEntity.ok().build(); // Ou ResponseEntity.noContent().build() pour 204
        }
        return ResponseEntity.notFound().build();
    }
}