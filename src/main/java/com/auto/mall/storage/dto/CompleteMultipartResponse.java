package com.auto.mall.storage.dto;

public record CompleteMultipartResponse(
        String objectKey,
        String publicUrl
) {}