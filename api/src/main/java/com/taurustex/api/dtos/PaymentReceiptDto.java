package com.taurustex.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptDto {
    private String fullNumber;
    private String receiptNumber;
    private String studentName;
    private String receiptDate;
    private String amountInWords;
    private String paymentReason;
    private String academicYear;
    private String amountInFigures;
    private String currency;

    // Streams d'images
    private InputStream logoSchool;
    private InputStream qrCodeImage;

    // Hash cryptographique
    private String securityHash;

    /**
     * Calcule et définit l'empreinte SHA-256 unique du reçu.
     *
     * @param secretKey Clé secrète de signature
     * @return L'empreinte SHA-256 générée
     */
    public String generateAndSetSHA256Hash(String secretKey) {
        try {
            String rawData = String.format("%s|%s|%s|%s|%s",
                     studentName, amountInFigures, receiptDate, paymentReason, secretKey);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            this.securityHash = hexString.toString().toUpperCase();
            return this.securityHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de calcul du Hash SHA-256", e);
        }
    }

    public void generateReceiptNumber(){
        this.receiptNumber = this.fullNumber.substring(4,8)+this.fullNumber.substring(18,22);
    }

    /**
     * Convertit le DTO en Map pour JasperReports.
     */
    public Map<String, Object> toReportParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("receiptNumber", this.receiptNumber);
        params.put("studentName", this.studentName);
        params.put("receiptDate", this.receiptDate);
        params.put("amountInWords", this.amountInWords);
        params.put("paymentReason", this.paymentReason);
        params.put("academicYear", this.academicYear);
        params.put("amountInFigures", this.amountInFigures);
        params.put("currency", this.currency != null ? this.currency : "F CFA");
        params.put("logoSchool", this.logoSchool);
        params.put("qrCodeImage", this.qrCodeImage);
        params.put("securityHash", this.securityHash);
        return params;
    }


}
