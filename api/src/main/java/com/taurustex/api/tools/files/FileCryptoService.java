package com.taurustex.api.tools.files;

public interface FileCryptoService {
    byte[] encrypt(byte[] data) throws Exception;

    byte[] decrypt(byte[] combined) throws Exception;
}
