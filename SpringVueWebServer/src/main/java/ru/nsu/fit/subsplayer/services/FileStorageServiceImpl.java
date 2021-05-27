package ru.nsu.fit.subsplayer.services;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.fit.subsplayer.constants.Locations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileStorageServiceImpl implements FileStorageService {

    private static final Path FILE_STORAGE_LOCATION = Paths.get(Locations.VIDEO);

    public FileStorageServiceImpl() {
        try {
            Files.createDirectories(FILE_STORAGE_LOCATION);
        } catch (Exception e) {
            throw new RuntimeException("Could not create the directory for uploaded files", e);
        }
    }

    @Override
    public void storeFile(String fileName, MultipartFile file) {
        try {
            Files.copy(
                file.getInputStream(),
                FILE_STORAGE_LOCATION.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + fileName, e);
        }
    }
}
