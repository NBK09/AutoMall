package com.auto.mall.ad.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdEditResponse(
        Long id,
        Long brandId,
        Long modelId,
        Long generationId,
        Long engineId,
        Long transmissionId,
        Long driveTypeId,
        Long cityId,
        Integer year,
        Integer mileage,
        String color,
        String vin,
        BigDecimal price,
        String currency,
        String description,
        List<String> photoUrls,
        Integer mainIndex
) {
}
