package com.taurustex.api.tools.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    boolean store(MultipartFile file, String fileName);

    Resource read(String fileName);

    InputStream readAsStream(String fileName);

    void delete(String oldFileUrl);
}
