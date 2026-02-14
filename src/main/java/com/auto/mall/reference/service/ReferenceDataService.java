package com.auto.mall.reference.service;

import com.auto.mall.geo.dto.CityResponse;
import com.auto.mall.geo.dto.RegionResponse;
import com.auto.mall.geo.repository.CityRepository;
import com.auto.mall.geo.repository.RegionRepository;
import com.auto.mall.reference.dto.ReferenceDataResponse;
import com.auto.mall.vehicle.brand.dto.BrandResponse;
import com.auto.mall.vehicle.brand.repository.BrandRepository;
import com.auto.mall.vehicle.drive.dto.DriveTypeResponse;
import com.auto.mall.vehicle.drive.repository.DriveTypeRepository;
import com.auto.mall.vehicle.engine.dto.EngineResponse;
import com.auto.mall.vehicle.engine.repository.EngineRepository;
import com.auto.mall.vehicle.generation.dto.GenerationResponse;
import com.auto.mall.vehicle.generation.repository.GenerationRepository;
import com.auto.mall.vehicle.model.dto.ModelResponse;
import com.auto.mall.vehicle.model.repository.ModelRepository;
import com.auto.mall.vehicle.transmission.dto.TransmissionResponse;
import com.auto.mall.vehicle.transmission.repository.TransmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferenceDataService {

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final GenerationRepository generationRepository;
    private final EngineRepository engineRepository;
    private final TransmissionRepository transmissionRepository;
    private final DriveTypeRepository driveTypeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    public ReferenceDataResponse getFull() {

        return new ReferenceDataResponse(
                brandRepository.findAll().stream()
                        .map(b -> new BrandResponse(b.getId(), b.getName()))
                        .toList(),

                modelRepository.findAll().stream()
                        .map(m -> new ModelResponse(
                                m.getId(),
                                m.getName(),
                                m.getBrand().getId()
                        ))
                        .toList(),

                generationRepository.findAll().stream()
                        .map(g -> new GenerationResponse(
                                g.getId(),
                                g.getName(),
                                g.getModel().getId()
                        ))
                        .toList(),

                engineRepository.findAll().stream()
                        .map(e -> new EngineResponse(
                                e.getId(),
                                e.getName(),
                                e.getVolume(),
                                e.getFuelType(),
                                e.getGeneration().getId()
                        ))
                        .toList(),

                transmissionRepository.findAll().stream()
                        .map(t -> new TransmissionResponse(t.getId(), t.getName()))
                        .toList(),

                driveTypeRepository.findAll().stream()
                        .map(d -> new DriveTypeResponse(d.getId(), d.getName()))
                        .toList(),

                regionRepository.findAll().stream()
                        .map(r -> new RegionResponse(r.getId(), r.getName()))
                        .toList(),

                cityRepository.findAll().stream()
                        .map(c -> new CityResponse(
                                c.getId(),
                                c.getName(),
                                c.getRegion().getId()
                        ))
                        .toList()
        );
    }
}