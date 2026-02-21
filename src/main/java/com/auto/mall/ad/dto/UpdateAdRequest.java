package com.auto.mall.ad.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record UpdateAdRequest(
        @NotNull Long brandId,
        @NotNull Long modelId,
        @NotNull Long generationId,

        @NotNull Long engineId,
        @NotNull Long transmissionId,
        @NotNull Long driveTypeId,
        @NotNull Long cityId,

        @NotNull @Min(1900) @Max(2100) Integer year,
        @NotNull @Min(0) Integer mileage,

        @NotBlank String color,
        String vin,

        @NotNull @Positive BigDecimal price,
        @NotBlank @Size(min = 5, max = 5000) String description,

        List<String> photoUrls,
        Integer mainIndex
) {
}
