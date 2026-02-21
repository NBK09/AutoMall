package com.auto.mall.ad.dto;

import java.util.List;

public record PhotoUploadResponse(
        List<String> photoUrls
) {
}
