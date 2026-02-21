package com.auto.mall.ad.dto;

import com.auto.mall.ad.Enum.AdStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdDetailsResponse(
        Long id,
        AdStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        String brand,
        String model,
        String generation,
        String engine,
        String transmission,
        String driveType,

        Integer year,
        Integer mileage,
        String color,
        String vin,
        BigDecimal price,
        String currency,
        String description,
        String city,

        List<String> photoUrls,
        SellerResponse seller
) {
    public record SellerResponse(
            Long id,
            String telegramUsername,
            Long telegramUserId
    ) {
    }
}
