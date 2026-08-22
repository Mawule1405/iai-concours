package com.taurustex.api.tools.files;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/tsc-api/files")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                           @RequestParam(value = "fileName") String fileName
    ) {
        boolean answer = storageService.store(file, fileName);
        if (answer) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName, HttpServletRequest request) {
        try {
            Resource resource = storageService.read(fileName);

            // Vérification cruciale : la ressource existe-t-elle ?
            if (resource == null || !resource.exists() || !resource.isReadable()) {
                log.warn("Fichier non trouvé ou illisible : {}", fileName);
                return ResponseEntity.notFound().build();
            }

            String contentType = null;
            try {
                // Utilisation sécurisée de getFile()
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.info("Détermination du type MIME impossible pour : {}", fileName);
            }

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            log.error("Erreur lors du téléchargement de {} : {}", fileName, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
