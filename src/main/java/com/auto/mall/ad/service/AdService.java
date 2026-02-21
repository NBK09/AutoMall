package com.auto.mall.ad.service;

import com.auto.mall.ad.Enum.AdStatus;
import com.auto.mall.ad.dto.AdDetailsResponse;
import com.auto.mall.ad.dto.AdEditResponse;
import com.auto.mall.ad.dto.AdResponse;
import com.auto.mall.ad.dto.CreateAdRequest;
import com.auto.mall.ad.dto.UpdateAdRequest;
import com.auto.mall.ad.entity.Ad;
import com.auto.mall.ad.entity.AdPhoto;
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
import java.util.*;

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

    private static final int MAX_PHOTOS_PER_AD = 10;

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

        validateRelations(brand, model, generation, engine);

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
                .status(AdStatus.ACTIVE)
                .active(true)
                .build();

        syncPhotos(ad, request.photoUrls(), null);
        return mapSummary(adRepository.save(ad));
    }

    @Transactional(readOnly = true)
    public AdDetailsResponse getDetails(Long adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));
        return mapDetails(ad);
    }

    @Transactional(readOnly = true)
    public AdEditResponse getForEdit(Long adId, Long userId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));
        ensureOwner(ad, userId);

        List<AdPhoto> orderedPhotos = getOrderedPhotos(ad);
        List<String> photoUrls = orderedPhotos.stream().map(AdPhoto::getPublicUrl).toList();

        int mainIndex = orderedPhotos.isEmpty() ? -1 : 0;
        for (int i = 0; i < orderedPhotos.size(); i++) {
            if (Boolean.TRUE.equals(orderedPhotos.get(i).getIsMain())) {
                mainIndex = i;
                break;
            }
        }

        return new AdEditResponse(
                ad.getId(),
                ad.getBrand().getId(),
                ad.getModel().getId(),
                ad.getGeneration().getId(),
                ad.getEngine().getId(),
                ad.getTransmission().getId(),
                ad.getDriveType().getId(),
                ad.getCity().getId(),
                ad.getYear(),
                ad.getMileage(),
                ad.getColor(),
                ad.getVin(),
                ad.getPrice(),
                ad.getCurrency(),
                ad.getDescription(),
                photoUrls,
                mainIndex < 0 ? null : mainIndex
        );
    }

    public AdDetailsResponse updateAd(Long adId, Long userId, UpdateAdRequest request) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found"));
        ensureOwner(ad, userId);

        if (ad.getStatus() != AdStatus.ACTIVE) {
            throw new IllegalArgumentException("Archived ads cannot be edited. Restore first.");
        }

        validateImmutableIds(ad, request.brandId(), request.modelId(), request.generationId());

        Engine engine = engineRepository.findById(request.engineId())
                .orElseThrow(() -> new EntityNotFoundException("Engine not found"));
        if (engine.getGeneration() == null || !engine.getGeneration().getId().equals(ad.getGeneration().getId())) {
            throw new IllegalArgumentException("Engine does not belong to ad generation");
        }

        Transmission transmission = transmissionRepository.findById(request.transmissionId())
                .orElseThrow(() -> new EntityNotFoundException("Transmission not found"));
        DriveType driveType = driveTypeRepository.findById(request.driveTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Drive type not found"));
        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new EntityNotFoundException("City not found"));

        ad.setEngine(engine);
        ad.setTransmission(transmission);
        ad.setDriveType(driveType);
        ad.setCity(city);

        ad.setYear(request.year());
        ad.setMileage(request.mileage());
        ad.setColor(request.color());
        ad.setVin(request.vin());
        ad.setPrice(request.price());
        ad.setCurrency(city.getRegion().getCountry().getCurrencyCode());
        ad.setDescription(request.description());

        if (request.photoUrls() != null) {
            syncPhotos(ad, request.photoUrls(), request.mainIndex());
        }

        return mapDetails(adRepository.save(ad));
    }

    @Transactional(readOnly = true)
    public List<AdResponse> getAllActiveAds() {
        return adRepository.findByStatusOrderByCreatedAtDesc(AdStatus.ACTIVE)
                .stream().map(this::mapSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<AdResponse> getMyByStatus(Long userId, AdStatus status) {
        return adRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
                .stream().map(this::mapSummary).toList();
    }

    public void archive(Long adId, Long userId) {
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new EntityNotFoundException("Ad not found"));
        ensureOwner(ad, userId);
        if (ad.getStatus() == AdStatus.ARCHIVED) return;
        ad.setStatus(AdStatus.ARCHIVED);
        ad.setActive(false);
        ad.setUpdatedAt(LocalDateTime.now());
    }

    public void restore(Long adId, Long userId) {
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new EntityNotFoundException("Ad not found"));
        ensureOwner(ad, userId);
        if (ad.getStatus() == AdStatus.ACTIVE) return;
        ad.setStatus(AdStatus.ACTIVE);
        ad.setActive(true);
        ad.setUpdatedAt(LocalDateTime.now());
    }

    static void validateImmutableIds(Ad ad, Long brandId, Long modelId, Long generationId) {
        if (brandId != null && !Objects.equals(brandId, ad.getBrand().getId())) {
            throw new IllegalArgumentException("brandId cannot be changed");
        }
        if (modelId != null && !Objects.equals(modelId, ad.getModel().getId())) {
            throw new IllegalArgumentException("modelId cannot be changed");
        }
        if (generationId != null && !Objects.equals(generationId, ad.getGeneration().getId())) {
            throw new IllegalArgumentException("generationId cannot be changed");
        }
    }

    private void validateRelations(Brand brand, Model model, Generation generation, Engine engine) {
        if (!model.getBrand().getId().equals(brand.getId())) {
            throw new IllegalArgumentException("Model does not belong to brand");
        }
        if (!generation.getModel().getId().equals(model.getId())) {
            throw new IllegalArgumentException("Generation does not belong to model");
        }
        if (engine.getGeneration() == null || !engine.getGeneration().getId().equals(generation.getId())) {
            throw new IllegalArgumentException("Engine does not belong to generation");
        }
    }

    static String extractS3Key(String photoUrl) {
        String url = photoUrl == null ? "" : photoUrl.trim();
        if (url.isBlank()) throw new IllegalArgumentException("Photo URL is invalid: empty value");

        int queryIndex = url.indexOf('?');
        if (queryIndex >= 0) url = url.substring(0, queryIndex);
        int fragmentIndex = url.indexOf('#');
        if (fragmentIndex >= 0) url = url.substring(0, fragmentIndex);

        int slashIndex = url.lastIndexOf('/');
        String key = slashIndex >= 0 ? url.substring(slashIndex + 1) : url;
        if (key.isBlank()) throw new IllegalArgumentException("Photo URL is invalid: cannot extract s3_key");
        return key;
    }

    static List<String> sanitizePhotoUrls(List<String> photoUrls) {
        if (photoUrls == null) return List.of();
        if (photoUrls.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("photoUrls contains null values");
        }

        List<String> sanitized = new ArrayList<>(new LinkedHashSet<>(photoUrls.stream()
                .map(String::trim)
                .toList()));

        if (sanitized.stream().anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("photoUrls contains blank values");
        }
        if (sanitized.size() > MAX_PHOTOS_PER_AD) {
            throw new IllegalArgumentException("Ad can contain up to 10 photos");
        }
        return sanitized;
    }

    static int resolveMainIndex(int photosSize, Integer mainIndex) {
        if (photosSize == 0) return -1;
        int resolved = mainIndex == null ? 0 : mainIndex;
        if (resolved < 0 || resolved >= photosSize) {
            throw new IllegalArgumentException("mainIndex is out of range");
        }
        return resolved;
    }

    void syncPhotos(Ad ad, List<String> photoUrlsInput, Integer mainIndex) {
        List<AdPhoto> ordered = mergePhotos(ad, ad.getPhotos(), photoUrlsInput, mainIndex);
        ad.getPhotos().clear();
        ad.getPhotos().addAll(ordered);
    }

    static List<AdPhoto> mergePhotos(Ad ad, List<AdPhoto> existingPhotos, List<String> photoUrlsInput, Integer mainIndex) {
        List<String> photoUrls = sanitizePhotoUrls(photoUrlsInput);
        int resolvedMainIndex = resolveMainIndex(photoUrls.size(), mainIndex);

        Map<String, AdPhoto> existingByUrl = new HashMap<>();
        for (AdPhoto photo : existingPhotos) {
            if (photo.getPublicUrl() != null && !photo.getPublicUrl().isBlank()) {
                existingByUrl.put(photo.getPublicUrl(), photo);
            }
        }

        List<AdPhoto> ordered = new ArrayList<>();
        for (int i = 0; i < photoUrls.size(); i++) {
            String url = photoUrls.get(i);
            AdPhoto photo = existingByUrl.get(url);
            if (photo == null) {
                photo = AdPhoto.builder().ad(ad).publicUrl(url).s3Key(extractS3Key(url)).build();
            }
            if (photo.getS3Key() == null || photo.getS3Key().isBlank()) {
                photo.setS3Key(extractS3Key(url));
            }
            photo.setPublicUrl(url);
            photo.setSortOrder(i);
            photo.setIsMain(i == resolvedMainIndex);
            ordered.add(photo);
        }
        return ordered;
    }

    private void ensureOwner(Ad ad, Long userId) {
        if (!ad.getUser().getId().equals(userId)) throw new AccessDeniedException("Not your ad");
    }

    private List<AdPhoto> getOrderedPhotos(Ad ad) {
        return ad.getPhotos().stream()
                .sorted(Comparator.comparing(AdPhoto::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .filter(photo -> photo.getPublicUrl() != null && !photo.getPublicUrl().isBlank())
                .toList();
    }

    private AdResponse mapSummary(Ad ad) {
        List<String> photoUrls = getOrderedPhotos(ad).stream().map(AdPhoto::getPublicUrl).toList();
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
                ad.getUser().getId(),
                photoUrls
        );
    }

    private AdDetailsResponse mapDetails(Ad ad) {
        List<String> photoUrls = getOrderedPhotos(ad).stream().map(AdPhoto::getPublicUrl).toList();
        User seller = ad.getUser();
        return new AdDetailsResponse(
                ad.getId(),
                ad.getStatus(),
                ad.getCreatedAt(),
                ad.getUpdatedAt(),
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
                ad.getDescription(),
                ad.getCity().getName(),
                photoUrls,
                new AdDetailsResponse.SellerResponse(seller.getId(), seller.getUsername(), seller.getTelegramId())
        );
    }
}
