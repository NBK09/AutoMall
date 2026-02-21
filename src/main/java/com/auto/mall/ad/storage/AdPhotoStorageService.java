package com.auto.mall.ad.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdPhotoStorageService {

    private static final int MAX_FILES_PER_REQUEST = 10;

    private final Path uploadRoot;

    public AdPhotoStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
    }

    public List<String> store(List<MultipartFile> files) throws IOException {
        List<String> result = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return result;
        }

        long nonEmptyFiles = files.stream().filter(file -> file != null && !file.isEmpty()).count();
        if (nonEmptyFiles > MAX_FILES_PER_REQUEST) {
            throw new IllegalArgumentException("You can upload up to 10 photos at a time");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are supported");
            }

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + (extension != null ? "." + extension : "");
            Path target = uploadRoot.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            result.add("/uploads/" + fileName);
        }
        return result;
    }
}
