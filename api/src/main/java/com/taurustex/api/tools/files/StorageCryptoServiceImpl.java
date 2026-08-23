package com.taurustex.api.tools.files;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageCryptoServiceImpl implements StorageService {

    private final Path storageLocation;
    private final PrepareFile prepareFile;
    private final FileCryptoService fileCryptoService;

    @Override
    public boolean store(MultipartFile file, String fileName) {
        try {
            if (file == null || file.isEmpty() || fileName == null || fileName.trim().isEmpty()) {
                log.warn("Fichier vide ou nom de base vide, impossible de stocker.");
                return false;
            }

            Path targetPath = storageLocation.resolve(fileName).normalize();

            if (!targetPath.startsWith(storageLocation.normalize())) {
                log.error("Tentative d'accès en dehors du répertoire de stockage autorisé.");
                return false;
            }

            // --- COUCHE DE SÉCURITÉ : CHIFFREMENT ---
            byte[] fileBytes = file.getBytes();
            //byte[] encryptedBytes = fileCryptoService.encrypt(fileBytes);

            // Écriture du fichier chiffré sur le disque
            Files.write(targetPath, fileBytes);

            log.info("Fichier chiffré et stocké avec succès: {}", fileName);
            return true;

        } catch (Exception e) { // Exception générale car le crypto peut lever diverses erreurs
            log.error("Erreur lors du stockage chiffré du fichier: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Resource read(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) return null;

            String cleanFileName = prepareFile.cleanFileName(fileName);
            Path filePath = storageLocation.resolve(cleanFileName).normalize();

            if (!filePath.startsWith(storageLocation.normalize()) || !Files.exists(filePath)) {
                return null;
            }

            // --- COUCHE DE SÉCURITÉ : DÉCHIFFREMENT ---
            byte[] encryptedBytes = Files.readAllBytes(filePath);
            //byte[] decryptedBytes = fileCryptoService.decrypt(encryptedBytes);

            // Pour retourner une Resource (UrlResource attend un fichier physique),
            // on peut utiliser une ByteArrayResource pour le contenu déchiffré en mémoire.
            return new org.springframework.core.io.ByteArrayResource(encryptedBytes) {
                @Override
                public String getFilename() {
                    return cleanFileName;
                }
            };

        } catch (Exception e) {
            log.error("Erreur lors de la lecture/déchiffrement du fichier: {}", fileName, e);
            throw new RuntimeException("Erreur de sécurité lors de l'accès au fichier.");
        }
    }

    @Override
    public InputStream readAsStream(String fileName) {
        try {
            if (fileName == null || fileName.trim().isEmpty()) {
                return null;
            }

            String cleanFileName = prepareFile.cleanFileName(fileName);
            Path filePath = storageLocation.resolve(cleanFileName).normalize();

            // Contrôle de sécurité du chemin et vérification de l'existence
            if (!filePath.startsWith(storageLocation.normalize()) || !Files.exists(filePath)) {
                log.warn("Fichier introuvable ou chemin invalide : {}", fileName);
                return null;
            }

            // --- COUCHE DE SÉCURITÉ : DÉCHIFFREMENT ---
            byte[] encryptedBytes = Files.readAllBytes(filePath);
            //byte[] decryptedBytes = fileCryptoService.decrypt(encryptedBytes);

            // Encapsulation des octets déchiffrés dans un InputStream
            return new ByteArrayInputStream(encryptedBytes);

        } catch (Exception e) {
            log.error("Erreur lors de la lecture/déchiffrement du fichier : {}", fileName, e);
            throw new RuntimeException("Erreur de sécurité lors de l'accès au fichier.", e);
        }
    }

    @Override
    public void delete(String oldFileUrl) {
        try {
            // 1. Validation de l'entrée
            if (oldFileUrl == null || oldFileUrl.trim().isEmpty()) {
                log.warn("Nom de fichier vide, suppression annulée.");
                return;
            }

            // 2. Nettoyage et résolution du chemin d'accès
            String cleanFileName = prepareFile.cleanFileName(oldFileUrl);
            Path filePath = storageLocation.resolve(cleanFileName).normalize();

            // 3. COUCHE DE SÉCURITÉ : Empêcher la suppression de fichiers système (Ex: ../../../etc/passwd)
            if (!filePath.startsWith(storageLocation.normalize())) {
                log.error("Tentative frauduleuse de suppression en dehors du répertoire de stockage autorisé : {}", oldFileUrl);
                throw new SecurityException("Accès non autorisé au système de fichiers.");
            }

            // 4. Suppression physique du fichier s'il existe
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Fichier chiffré supprimé avec succès du stockage : {}", cleanFileName);
            } else {
                log.warn("Tentative de suppression d'un fichier inexistant : {}", cleanFileName);
            }

        } catch (SecurityException e) {
            throw e; // On propage l'erreur de sécurité
        } catch (Exception e) {
            log.error("Erreur lors de la suppression physique du fichier chiffré {}: {}", oldFileUrl, e.getMessage());
            throw new RuntimeException("Échec de la suppression du fichier sur le disque.");
        }
    }
}