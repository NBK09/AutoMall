//package com.auto.mall.ad.service;
//
//import com.auto.mall.ad.entity.Ad;
//import com.auto.mall.ad.entity.AdPhoto;
//import com.auto.mall.ad.repository.AdPhotoRepository;
//import com.auto.mall.ad.repository.AdRepository;
//import com.auto.mall.storage.S3MultipartService;
//import com.auto.mall.storage.dto.*;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class AdPhotoService {
//
//    private static final int MAX_PHOTOS = 10;
//
//    private final AdRepository adRepository;
//    private final AdPhotoRepository adPhotoRepository;
//    private final S3MultipartService s3MultipartService;
//
//    public InitMultipartResponse init(Long adId, Long userId, String contentType, String ext) {
//        Ad ad = getMyAd(adId, userId);
//
//        long count = adPhotoRepository.countByAdId(adId);
//        if (count >= MAX_PHOTOS) {
//            throw new IllegalStateException("Max photos reached (10)");
//        }
//
//        String objectKey = s3MultipartService.buildObjectKey(adId, ext);
//        return s3MultipartService.init(adId, objectKey, contentType);
//    }
//
//    public PresignPartsResponse presignParts(Long adId, Long userId, PresignPartsRequest req) {
//        getMyAd(adId, userId);
//        return s3MultipartService.presignParts(req);
//    }
//
//    public AdPhoto complete(Long adId, Long userId, CompleteMultipartRequest req) {
//        Ad ad = getMyAd(adId, userId);
//
//        long count = adPhotoRepository.countByAdId(adId);
//        if (count >= MAX_PHOTOS) {
//            // если уже 10 — лучше abort (но upload уже завершится, так что можно deleteObject)
//            throw new IllegalStateException("Max photos reached (10)");
//        }
//
//        CompleteMultipartResponse completed = s3MultipartService.complete(req);
//
//        int nextOrder = (int) count + 1;
//
//        AdPhoto photo = AdPhoto.builder()
//                .ad(ad)
//                .objectKey(completed.objectKey())
//                .url(completed.publicUrl())
//                .sortOrder(nextOrder)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        return adPhotoRepository.save(photo);
//    }
//
//    public void delete(Long adId, Long photoId, Long userId) {
//        Ad ad = getMyAd(adId, userId);
//        AdPhoto photo = adPhotoRepository.findById(photoId)
//                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));
//        if (!photo.getAd().getId().equals(ad.getId())) {
//            throw new AccessDeniedException("Photo not in this ad");
//        }
//
//        s3MultipartService.deleteObject(photo.getObjectKey());
//        adPhotoRepository.delete(photo);
//    }
//
//    private Ad getMyAd(Long adId, Long userId) {
//        Ad ad = adRepository.findById(adId)
//                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));
//        if (!ad.getUser().getId().equals(userId)) {
//            throw new AccessDeniedException("Not your ad");
//        }
//        return ad;
//    }
//}