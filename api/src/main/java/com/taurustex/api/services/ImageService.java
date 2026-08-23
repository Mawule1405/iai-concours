package com.taurustex.api.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    boolean storeImage(MultipartFile file);

    Resource readImage();
}
