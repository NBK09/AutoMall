package com.auto.mall.storage.dto;

public record CompletedPartDto(
        int partNumber,
        String eTag
) {}