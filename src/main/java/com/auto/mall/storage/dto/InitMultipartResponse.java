package com.auto.mall.storage.dto;

public record InitMultipartResponse(
        String uploadId,
        String objectKey
) {}