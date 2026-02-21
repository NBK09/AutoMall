//package com.auto.mall.storage;
//
//import com.auto.mall.storage.dto.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
//import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;
//
//import java.time.Duration;
//import java.util.Comparator;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class S3MultipartService {
//
//    private final S3Client s3;
//    private final S3Presigner presigner;
//    private final S3Properties props;
//
//    /** Генерим ключ типа: ads/<adId>/<uuid>.jpg */
//    public String buildObjectKey(long adId, String ext) {
//        String safeExt = (ext == null || ext.isBlank()) ? "jpg" : ext.replace(".", "");
//        return props.getPhotosPrefix() + "/" + adId + "/" + UUID.randomUUID() + "." + safeExt;
//    }
//
//    public InitMultipartResponse init(long adId, String objectKey, String contentType) {
//        CreateMultipartUploadResponse resp = s3.createMultipartUpload(CreateMultipartUploadRequest.builder()
//                .bucket(props.getBucket())
//                .key(objectKey)
//                .contentType(contentType)
//                // можно метаданные: adId, userId и т.п.
//                .build());
//
//        return new InitMultipartResponse(resp.uploadId(), objectKey);
//    }
//
//    public PresignPartsResponse presignParts(PresignPartsRequest req) {
//        if (req.partCount() < 1 || req.partCount() > 10000) {
//            throw new IllegalArgumentException("Invalid partCount");
//        }
//
//        Duration ttl = Duration.ofSeconds(props.getPresignExpiresSeconds());
//
//        List<PresignedPart> parts = java.util.stream.IntStream.rangeClosed(1, req.partCount())
//                .mapToObj(partNumber -> {
//                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
//                            .bucket(props.getBucket())
//                            .key(req.objectKey())
//                            .uploadId(req.uploadId())
//                            .partNumber(partNumber)
//                            .build();
//
//                    PresignedUploadPartRequest presigned = presigner.presignUploadPart(
//                            UploadPartPresignRequest.builder()
//                                    .signatureDuration(ttl)
//                                    .uploadPartRequest(uploadPartRequest)
//                                    .build()
//                    );
//
//                    return new PresignedPart(partNumber, presigned.url().toString());
//                })
//                .toList();
//
//        return new PresignPartsResponse(req.uploadId(), req.objectKey(), parts);
//    }
//
//    public CompleteMultipartResponse complete(CompleteMultipartRequest req) {
//        // eTag обычно приходит с кавычками, лучше убрать
//        List<CompletedPart> completed = req.parts().stream()
//                .sorted(Comparator.comparingInt(CompletedPartDto::partNumber))
//                .map(p -> CompletedPart.builder()
//                        .partNumber(p.partNumber())
//                        .eTag(stripQuotes(p.eTag()))
//                        .build())
//                .toList();
//
//        s3.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
//                .bucket(props.getBucket())
//                .key(req.objectKey())
//                .uploadId(req.uploadId())
//                .multipartUpload(CompletedMultipartUpload.builder().parts(completed).build())
//                .build());
//
//        String publicUrl = buildPublicUrl(req.objectKey());
//        return new CompleteMultipartResponse(req.objectKey(), publicUrl);
//    }
//
//    public void abort(String uploadId, String objectKey) {
//        s3.abortMultipartUpload(AbortMultipartUploadRequest.builder()
//                .bucket(props.getBucket())
//                .key(objectKey)
//                .uploadId(uploadId)
//                .build());
//    }
//
//    public void deleteObject(String objectKey) {
//        s3.deleteObject(DeleteObjectRequest.builder()
//                .bucket(props.getBucket())
//                .key(objectKey)
//                .build());
//    }
//
//    public String buildPublicUrl(String objectKey) {
//        String base = props.getPublicBaseUrl();
//        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
//        return base + "/" + objectKey;
//    }
//
//    private static String stripQuotes(String eTag) {
//        if (eTag == null) return null;
//        return eTag.replace("\"", "");
//    }
//}