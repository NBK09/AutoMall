package com.auto.mall.storage.dto;

import java.util.List;

public record PresignPartsResponse(
        String uploadId,
        String objectKey,
        List<PresignedPart> parts
) {}