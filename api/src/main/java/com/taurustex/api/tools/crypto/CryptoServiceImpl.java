package com.taurustex.api.tools.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    private final byte[] secretKey;

    public CryptoServiceImpl(@Value("${file.encryption.key}") String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("La clé de chiffrement ne peut pas être vide.");
        }
        // Vérification de la taille de la clé AES (16, 24 ou 32 octets / caractères)
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("La clé AES doit faire 16, 24 ou 32 octets (caractères).");
        }
        this.secretKey = keyBytes;
    }

    @Override
    public String encrypt(String text) throws Exception {
        if (text == null) {
            return null;
        }

        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv); // IV aléatoire unique

        Cipher cipher = Cipher.getInstance(ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "AES"), spec);

        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = cipher.doFinal(textBytes);

        // Concaténation de l'IV + données chiffrées
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        // Encodage en Base64 pour obtenir une chaîne lisible
        return Base64.getEncoder().encodeToString(combined);
    }

    @Override
    public String decrypt(String encryptedTextBase64) throws Exception {
        if (encryptedTextBase64 == null) {
            return null;
        }

        byte[] combined = Base64.getDecoder().decode(encryptedTextBase64);

        if (combined.length < IV_LENGTH_BYTE) {
            throw new IllegalArgumentException("Le contenu chiffré est trop court/invalide.");
        }

        // Extraction de l'IV
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        Cipher cipher = Cipher.getInstance(ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "AES"), spec);

        // Déchiffrement du reste du tableau
        byte[] decryptedBytes = cipher.doFinal(combined, iv.length, combined.length - iv.length);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
