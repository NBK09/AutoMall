package com.auto.mall.reference.controller;

import com.auto.mall.geo.repository.CityRepository;
import com.auto.mall.reference.dto.IdNameDto;
import com.auto.mall.vehicle.brand.repository.BrandRepository;
import com.auto.mall.vehicle.drive.repository.DriveTypeRepository;
import com.auto.mall.vehicle.engine.repository.EngineRepository;
import com.auto.mall.vehicle.generation.repository.GenerationRepository;
import com.auto.mall.vehicle.model.repository.ModelRepository;
import com.auto.mall.vehicle.transmission.repository.TransmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final GenerationRepository generationRepository;
    private final EngineRepository engineRepository;
    private final TransmissionRepository transmissionRepository;
    private final DriveTypeRepository driveTypeRepository;
    private final CityRepository cityRepository;

    @GetMapping("/brands")
    public List<IdNameDto> brands() {
        return brandRepository.findByActiveTrueOrderByNameAsc()
                .stream().map(b -> new IdNameDto(b.getId(), b.getName()))
                .toList();
    }

    @GetMapping("/brands/{brandId}/models")
    public List<IdNameDto> models(@PathVariable Long brandId) {
        return modelRepository.findByBrandIdAndActiveTrueOrderByNameAsc(brandId)
                .stream().map(m -> new IdNameDto(m.getId(), m.getName()))
                .toList();
    }

    @GetMapping("/models/{modelId}/generations")
    public List<IdNameDto> generations(@PathVariable Long modelId) {
        return generationRepository.findByModelIdAndActiveTrueOrderByNameAsc(modelId)
                .stream().map(g -> new IdNameDto(g.getId(), g.getName()))
                .toList();
    }

    @GetMapping("/generations/{generationId}/engines")
    public List<IdNameDto> engines(@PathVariable Long generationId) {
        return engineRepository.findByGenerationIdAndActiveTrueOrderByNameAsc(generationId)
                .stream().map(e -> new IdNameDto(e.getId(), e.getName()))
                .toList();
    }

    @GetMapping("/transmissions")
    public List<IdNameDto> transmissions() {
        return transmissionRepository.findAllByOrderByNameAsc()
                .stream().map(t -> new IdNameDto(t.getId(), t.getName()))
                .toList();
    }

    @GetMapping("/drive-types")
    public List<IdNameDto> driveTypes() {
        return driveTypeRepository.findAllByOrderByNameAsc()
                .stream().map(d -> new IdNameDto(d.getId(), d.getName()))
                .toList();
    }

    @GetMapping("/cities")
    public List<IdNameDto> cities() {
        return cityRepository.findAllByOrderByNameAsc()
                .stream().map(c -> new IdNameDto(c.getId(), c.getName()))
                .toList();
    }
}