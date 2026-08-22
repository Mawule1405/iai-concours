package com.taurustex.api.tools.files;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PrepareFile {

    /**
     * Nettoie le nom de fichier pour éviter les injections de chemin
     */
    public String cleanFileName(String fileName) {
        if (fileName == null) {
            return "";
        }
        // Retourne seulement le nom du fichier (pas le chemin)
        return Paths.get(fileName).getFileName().toString();
    }

    public String generateFullPath(String originalFileName) {
        // Génère un UUID unique
        String uniqueCode = UUID.randomUUID().toString();
        LocalDateTime dateTime = LocalDateTime.now();

        String ext = getExtension(originalFileName);
        return uniqueCode + "-" + dateTime.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"))+ext;
    }

    public String getExtension(String originalName) {
        if (originalName == null || originalName.isEmpty()) {
            return null;
        }

        int lastDotIndex = originalName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalName.length() - 1) {
            return originalName.substring(lastDotIndex); // Includes the dot (e.g., ".pdf")
        }

        return null; // No valid extension found
    }

}
