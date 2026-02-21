package com.auto.mall.storage.dto;

import java.util.List;

public record CompleteMultipartRequest(
        String uploadId,
        String objectKey,
        List<CompletedPartDto> parts
) {}