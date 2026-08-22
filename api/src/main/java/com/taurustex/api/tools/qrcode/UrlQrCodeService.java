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
public class UrlQrCodeService {

    /**
     * Génère un QR Code sous forme de tableau d'octets (PNG) à partir d'une URL.
     * Lors du scan, il redirige l'utilisateur vers l'adresse web spécifiée.
     *
     * @param url    L'URL de destination (ex: "https://taurustex.com" ou "www.taurustex.com")
     * @param width  Largeur de l'image en pixels (ex: 200)
     * @param height Hauteur de l'image en pixels (ex: 200)
     * @return Fichier PNG sous forme de byte[]
     */
    public byte[] generateUrlQrCode(String url, int width, int height) throws Exception {
        String formattedUrl = formatUrl(url);
        return generateQrCodeImage(formattedUrl, width, height);
    }

    /**
     * Génère un QR Code sous forme d'InputStream (pratique pour l'injection dans JasperReports).
     *
     * @param url    L'URL de destination
     * @param width  Largeur de l'image
     * @param height Hauteur de l'image
     * @return InputStream pointant sur l'image PNG du QR Code
     */
    public InputStream generateUrlQrCodeStream(String url, int width, int height) throws Exception {
        byte[] qrBytes = generateUrlQrCode(url, width, height);
        return new ByteArrayInputStream(qrBytes);
    }

    /**
     * S'assure que l'URL est valide et commence par un protocole web (http:// ou https://).
     * Si aucun protocole n'est présent, 'https://' est ajouté par défaut.
     */
    private String formatUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("L'URL ne peut pas être vide.");
        }

        String cleanUrl = url.trim();

        // Si le protocole web n'est pas spécifié, on ajoute "https://" par défaut
        if (!cleanUrl.toLowerCase().startsWith("http://") && !cleanUrl.toLowerCase().startsWith("https://")) {
            return "https://" + cleanUrl;
        }
        return cleanUrl;
    }

    /**
     * Méthode générique de génération d'un QR Code en image PNG à partir d'un texte/URL
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