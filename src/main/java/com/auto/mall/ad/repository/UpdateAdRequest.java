package com.auto.mall.ad.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateAdRequest(

        @Positive Integer mileage,

        @NotBlank String color,

        @Positive BigDecimal price,

        @NotBlank String description
) {}