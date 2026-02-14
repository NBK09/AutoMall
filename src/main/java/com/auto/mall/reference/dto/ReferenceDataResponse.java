package com.auto.mall.reference.dto;

import com.auto.mall.geo.dto.CityResponse;
import com.auto.mall.geo.dto.RegionResponse;
import com.auto.mall.vehicle.brand.dto.BrandResponse;
import com.auto.mall.vehicle.drive.dto.DriveTypeResponse;
import com.auto.mall.vehicle.engine.dto.EngineResponse;
import com.auto.mall.vehicle.generation.dto.GenerationResponse;
import com.auto.mall.vehicle.model.dto.ModelResponse;
import com.auto.mall.vehicle.transmission.dto.TransmissionResponse;

import java.util.List;

public record ReferenceDataResponse(

        List<BrandResponse> brands,
        List<ModelResponse> models,
        List<GenerationResponse> generations,
        List<EngineResponse> engines,
        List<TransmissionResponse> transmissions,
        List<DriveTypeResponse> driveTypes,
        List<RegionResponse> regions,
        List<CityResponse> cities
) {

    public record SimpleDto(
            Long id,
            String name
    ) {}

    public record ModelDto(
            Long id,
            String name,
            Long brandId
    ) {}

    public record GenerationDto(
            Long id,
            String name,
            Long modelId
    ) {}

    public record EngineDto(
            Long id,
            String name,
            Long generationId
    ) {}

    public record CityDto(
            Long id,
            String name,
            Long regionId
    ) {}
}