package com.auto.mall.ad.controllers;

import com.auto.mall.ad.dto.PhotoUploadResponse;
import com.auto.mall.ad.storage.AdPhotoStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-photos")
public class AdPhotoController {

    private final AdPhotoStorageService adPhotoStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoUploadResponse upload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return new PhotoUploadResponse(adPhotoStorageService.store(files));
    }
}
