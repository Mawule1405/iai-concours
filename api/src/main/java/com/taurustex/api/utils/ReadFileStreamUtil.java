package com.taurustex.api.utils;


import com.taurustex.api.tools.files.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Log4j2
public class ReadFileStreamUtil {

    private final StorageService storageService;

    public InputStream loadImageStream(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        try {
            InputStream content = storageService.readAsStream(fileName);
            if (content instanceof InputStream is) {
                return is;
            }
        } catch (Exception e) {
            log.warn("Impossible de charger la ressource d'image [{}]: {}", fileName, e.getMessage());
        }
        return null;
    }
}
