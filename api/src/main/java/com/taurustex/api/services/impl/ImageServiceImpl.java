package com.taurustex.api.services.impl;


import com.taurustex.api.services.ImageService;
import com.taurustex.api.tools.files.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final StorageService storageService;

    @Override
    public boolean storeImage(MultipartFile file){
        return storageService.store(file, "iai-logo.png");
    }

    @Override
    public Resource readImage(){
        try {
            return storageService.read("iai-logo.png");
        }catch (Exception e){
            return null;
        }
    }

}
