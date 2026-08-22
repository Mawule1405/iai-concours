package com.taurustex.api.tools.files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;


@Service
public class FileCryptoServiceImpl implements FileCryptoService {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;


    private final byte[] secretKey;

    public FileCryptoServiceImpl(@Value("${file.encryption.key}") String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("La clé de chiffrement ne peut pas être vide.");
        }
        this.secretKey = key.getBytes();
    }

    @Override
    public byte[] encrypt(byte[] data) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv); // Générer un IV unique par fichier

        Cipher cipher = Cipher.getInstance(ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "AES"), spec);

        byte[] encryptedData = cipher.doFinal(data);

        // On concatène l'IV au début pour pouvoir déchiffrer plus tard
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }

    @Override
    public byte[] decrypt(byte[] combined) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        Cipher cipher = Cipher.getInstance(ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "AES"), spec);

        return cipher.doFinal(combined, iv.length, combined.length - iv.length);
    }
}
