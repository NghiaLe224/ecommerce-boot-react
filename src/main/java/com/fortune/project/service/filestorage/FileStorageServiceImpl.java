package com.fortune.project.service.filestorage;

import com.fortune.project.exception.ApiException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Value("${upload.path}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new ApiException("Image file is empty");
        }

        String contentType = image.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new ApiException("Only image files are allowed");
        }

        String extension = FilenameUtils.getExtension(image.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ApiException("File extension not allowed: " + extension);
        }

        String fileName = UUID.randomUUID() + "." + extension;
        Path targetPath = Paths.get(uploadDir).resolve(fileName);

        try {
            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/images/" + fileName; // Trả lại đường dẫn client dùng được
        } catch (IOException e) {
            throw new ApiException("Failed to store image: " + e.getMessage());
        }
    }
}
