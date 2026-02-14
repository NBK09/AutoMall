package com.auto.mall.ad.service;

import com.auto.mall.ad.Enum.AdStatus;
import com.auto.mall.ad.dto.AdResponse;
import com.auto.mall.ad.dto.CreateAdRequest;
import com.auto.mall.ad.entity.Ad;
import com.auto.mall.ad.repository.AdRepository;
import com.auto.mall.geo.entity.City;
import com.auto.mall.geo.repository.CityRepository;
import com.auto.mall.user.entity.User;
import com.auto.mall.user.repository.UserRepository;
import com.auto.mall.vehicle.brand.entity.Brand;
import com.auto.mall.vehicle.brand.repository.BrandRepository;
import com.auto.mall.vehicle.drive.entity.DriveType;
import com.auto.mall.vehicle.drive.repository.DriveTypeRepository;
import com.auto.mall.vehicle.engine.entity.Engine;
import com.auto.mall.vehicle.engine.repository.EngineRepository;
import com.auto.mall.vehicle.generation.entity.Generation;
import com.auto.mall.vehicle.generation.repository.GenerationRepository;
import com.auto.mall.vehicle.model.entity.Model;
import com.auto.mall.vehicle.model.repository.ModelRepository;
import com.auto.mall.vehicle.transmission.entity.Transmission;
import com.auto.mall.vehicle.transmission.repository.TransmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final GenerationRepository generationRepository;
    private final EngineRepository engineRepository;
    private final TransmissionRepository transmissionRepository;
    private final DriveTypeRepository driveTypeRepository;

    private final CityRepository cityRepository;

    public AdResponse createAd(CreateAdRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));

        Model model = modelRepository.findById(request.modelId())
                .orElseThrow(() -> new EntityNotFoundException("Model not found"));

        Generation generation = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("Generation not found"));

        Engine engine = engineRepository.findById(request.engineId())
                .orElseThrow(() -> new EntityNotFoundException("Engine not found"));

        Transmission transmission = transmissionRepository.findById(request.transmissionId())
                .orElseThrow(() -> new EntityNotFoundException("Transmission not found"));

        DriveType driveType = driveTypeRepository.findById(request.driveTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Drive type not found"));

        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new EntityNotFoundException("City not found"));

        // защита от несовместимых связок
        if (!model.getBrand().getId().equals(brand.getId())) {
            throw new IllegalArgumentException("Model does not belong to brand");
        }
        if (!generation.getModel().getId().equals(model.getId())) {
            throw new IllegalArgumentException("Generation does not belong to model");
        }
        if (engine.getGeneration() == null || !engine.getGeneration().getId().equals(generation.getId())) {
            throw new IllegalArgumentException("Engine does not belong to generation");
        }

        Ad ad = Ad.builder()
                .brand(brand)
                .model(model)
                .generation(generation)
                .engine(engine)
                .transmission(transmission)
                .driveType(driveType)
                .city(city)
                .user(user)

                .year(request.year())
                .mileage(request.mileage())
                .color(request.color())
                .vin(request.vin())
                .price(request.price())
                .currency(city.getRegion().getCountry().getCurrencyCode())
                .description(request.description())

                // Истина = статус
                .status(AdStatus.ACTIVE)
                // синхронизация для старых запросов
                .active(true)
                .build();

        // если у тебя createdAt/updatedAt не проставляются автоматически, можно раскомментить:
        // ad.setCreatedAt(LocalDateTime.now());
        // ad.setUpdatedAt(LocalDateTime.now());

        return map(adRepository.save(ad));
    }

    // ========== LISTS ==========

    @Transactional(readOnly = true)
    public List<AdResponse> getAllActiveAds() {
        // ВАЖНО: не active=true, а статус ACTIVE
        return adRepository.findByStatusOrderByCreatedAtDesc(AdStatus.ACTIVE)
                .stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public List<AdResponse> getMyByStatus(Long userId, AdStatus status) {
        return adRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
                .stream()
                .map(this::map)
                .toList();
    }

    // ========== ACTIONS ==========

    public void archive(Long adId, Long userId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not your ad");
        }

        if (ad.getStatus() == AdStatus.ARCHIVED) {
            return; // уже в архиве
        }

        ad.setStatus(AdStatus.ARCHIVED);
        ad.setActive(false); // синхронизируем
        ad.setUpdatedAt(LocalDateTime.now());
    }

    public void restore(Long adId, Long userId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));

        if (!ad.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not your ad");
        }

        if (ad.getStatus() == AdStatus.ACTIVE) {
            return; // уже активное
        }

        ad.setStatus(AdStatus.ACTIVE);
        ad.setActive(true); // синхронизируем
        ad.setUpdatedAt(LocalDateTime.now());
    }

    private AdResponse map(Ad ad) {
        return new AdResponse(
                ad.getId(),
                ad.getBrand().getName(),
                ad.getModel().getName(),
                ad.getGeneration().getName(),
                ad.getEngine().getName(),
                ad.getTransmission().getName(),
                ad.getDriveType().getName(),
                ad.getYear(),
                ad.getMileage(),
                ad.getColor(),
                ad.getVin(),
                ad.getPrice(),
                ad.getCurrency(),
                ad.getCity().getName(),
                ad.getDescription(),
                ad.getStatus(),
                ad.getCreatedAt(),
                ad.getUser().getId()
        );
    }
}