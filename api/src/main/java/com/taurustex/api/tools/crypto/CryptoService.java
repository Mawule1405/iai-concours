package com.taurustex.api.tools.crypto;

public interface CryptoService {
    String encrypt(String text) throws Exception;

    String decrypt(String encryptedTextBase64) throws Exception;
}
