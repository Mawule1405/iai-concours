package com.taurustex.api.tools.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class PathConfig {


    @Bean
    Path StorageConfig() {
        Path path =  Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
        try {
            return  Files.createDirectories(path);
        }catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
