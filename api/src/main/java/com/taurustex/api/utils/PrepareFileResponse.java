package com.taurustex.api.utils;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class PrepareFileResponse {

    /**
     * Méthode utilitaire pour construire la réponse HTTP avec les bons en-têtes PDF.
     */
    public static ResponseEntity<byte[]> buildPdfResponse(byte[] pdfContent, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(filename)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}
