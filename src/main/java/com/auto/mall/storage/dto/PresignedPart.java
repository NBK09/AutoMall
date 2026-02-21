package com.auto.mall.storage.dto;

public record PresignedPart(
        int partNumber,
        String url
) {}