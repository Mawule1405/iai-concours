package com.taurustex.api.controllers;


import com.taurustex.api.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/iai-concours-api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * Upload d'une nouvelle image.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> storeImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }

        boolean isStored = imageService.storeImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(isStored);
    }

    /**
     * Récupération et affichage d'une image par son nom de fichier / ID.
     */
    @GetMapping
    public ResponseEntity<Resource> getImage() {

        Resource resource = imageService.readImage();

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        // Détection dynamique du Content-Type (image/png, image/jpeg, etc.)
        String contentType = null;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}