package com.auto.mall.ad.dto;


import com.auto.mall.ad.Enum.AdStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdResponse(
        Long id,

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

        String city,
        String description,

        AdStatus status,
        LocalDateTime createdAt,

        Long userId, // чтобы фронт мог понять "моё/не моё"
        List<String> photoUrls,
        boolean isFavorite
) { }