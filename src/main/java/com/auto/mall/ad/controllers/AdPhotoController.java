//package com.auto.mall.ad.controllers;
//
//import com.auto.mall.ad.entity.AdPhoto;
//import com.auto.mall.ad.service.AdPhotoService;
//import com.auto.mall.storage.dto.*;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/ads/{adId}/photos")
//public class AdPhotoController {
//
//    private final AdPhotoService adPhotoService;
//
//    // 1) init multipart
//    @PostMapping("/multipart/init")
//    public InitMultipartResponse init(
//            @PathVariable Long adId,
//            @RequestParam String contentType,
//            @RequestParam(required = false) String ext,
//            Authentication authentication
//    ) {
//        Long userId = Long.valueOf(authentication.getName());
//        return adPhotoService.init(adId, userId, contentType, ext);
//    }
//
//    // 2) presign parts
//    @PostMapping("/multipart/presign")
//    public PresignPartsResponse presign(
//            @PathVariable Long adId,
//            @Valid @RequestBody PresignPartsRequest req,
//            Authentication authentication
//    ) {
//        Long userId = Long.valueOf(authentication.getName());
//        return adPhotoService.presignParts(adId, userId, req);
//    }
//
//    // 3) complete multipart + save to DB
//    @PostMapping("/multipart/complete")
//    public PhotoCreatedResponse complete(
//            @PathVariable Long adId,
//            @Valid @RequestBody CompleteMultipartRequest req,
//            Authentication authentication
//    ) {
//        Long userId = Long.valueOf(authentication.getName());
//        AdPhoto saved = adPhotoService.complete(adId, userId, req);
//        return new PhotoCreatedResponse(saved.getId(), saved.getUrl(), saved.getSortOrder());
//    }
//
//    // delete photo
//    @DeleteMapping("/{photoId}")
//    public void delete(
//            @PathVariable Long adId,
//            @PathVariable Long photoId,
//            Authentication authentication
//    ) {
//        Long userId = Long.valueOf(authentication.getName());
//        adPhotoService.delete(adId, photoId, userId);
//    }
//
//    public record PhotoCreatedResponse(Long id, String url, Integer sortOrder) {}
//}