package ru.nsu.fit.subsplayer.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void storeFile(String fileName, MultipartFile file);
}
