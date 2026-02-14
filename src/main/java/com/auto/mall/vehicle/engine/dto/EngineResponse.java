package com.auto.mall.vehicle.engine.dto;

public record EngineResponse(
        Long id,
        String name,
        Double volume,
        String fuelType,
        Long generationId
) {}