package com.auto.mall.storage.dto;

public record InitMultipartRequest(
        String contentType,
        long contentLength
) {}