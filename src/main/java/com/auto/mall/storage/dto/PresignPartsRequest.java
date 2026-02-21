package com.auto.mall.storage.dto;

public record PresignPartsRequest(
        String uploadId,
        String objectKey,
        int partCount
) {}