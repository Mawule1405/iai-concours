package com.taurustex.api.tools.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelQrCodeService {

    /**
     * Génère un QR Code sous forme de tableau d'octets (PNG) à partir d'un numéro de téléphone.
     * Lors du scan, il ouvrira l'application d'appel avec le numéro renseigné.
     *
     * @param phoneNumber Le numéro de téléphone (ex: "+22890000000" ou "90000000")
     * @param width       Largeur de l'image en pixels (ex: 200)
     * @param height      Hauteur de l'image en pixels (ex: 200)
     * @return Fichier PNG sous forme de byte[]
     */
    public byte[] generatePhoneCallQrCode(String phoneNumber, int width, int height) throws Exception {
        String formattedUri = formatPhoneUri(phoneNumber);
        return generateQrCodeImage(formattedUri, width, height);
    }

    /**
     * Génère un QR Code sous forme d'InputStream (très utile pour l'injecter dans JasperReports).
     *
     * @param phoneNumber Le numéro de téléphone
     * @param width       Largeur
     * @param height      Hauteur
     * @return InputStream pointant sur l'image PNG du QR Code
     */
    public InputStream generatePhoneCallQrCodeStream(String phoneNumber, int width, int height) throws Exception {
        byte[] qrBytes = generatePhoneCallQrCode(phoneNumber, width, height);
        return new ByteArrayInputStream(qrBytes);
    }

    /**
     * S'assure que le numéro de téléphone est préfixé par 'tel:'
     */
    private String formatPhoneUri(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone ne peut pas être vide.");
        }

        // Supprime les espaces inutiles
        String cleanPhone = phoneNumber.trim().replaceAll("\\s+", "");

        // Si le protocole tel: n'est pas déjà présent, on l'ajoute
        if (!cleanPhone.toLowerCase().startsWith("tel:")) {
            return "tel:" + cleanPhone;
        }
        return cleanPhone;
    }

    /**
     * Méthode générique de génération d'un QR Code en image PNG à partir d'un texte/URI
     */
    private byte[] generateQrCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Options du QR Code (Encodage UTF-8, Marges réduites, Correction d'erreur élevée)
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Marge (Quiet Zone) de 1 bloc
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Haut niveau de tolérance aux erreurs (30%)

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        }
    }
}