package com.auto.mall.initializer;

import com.auto.mall.geo.entity.City;
import com.auto.mall.geo.entity.Country;
import com.auto.mall.geo.entity.Region;
import com.auto.mall.geo.repository.CityRepository;
import com.auto.mall.geo.repository.CountryRepository;
import com.auto.mall.geo.repository.RegionRepository;
import com.auto.mall.vehicle.brand.entity.Brand;
import com.auto.mall.vehicle.brand.repository.BrandRepository;
import com.auto.mall.vehicle.model.entity.Model;
import com.auto.mall.vehicle.model.repository.ModelRepository;
import com.auto.mall.vehicle.generation.entity.Generation;
import com.auto.mall.vehicle.generation.repository.GenerationRepository;
import com.auto.mall.vehicle.engine.entity.Engine;
import com.auto.mall.vehicle.engine.repository.EngineRepository;
import com.auto.mall.vehicle.transmission.entity.Transmission;
import com.auto.mall.vehicle.transmission.repository.TransmissionRepository;
import com.auto.mall.vehicle.drive.entity.DriveType;
import com.auto.mall.vehicle.drive.repository.DriveTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReferenceDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final GenerationRepository generationRepository;
    private final EngineRepository engineRepository;
    private final TransmissionRepository transmissionRepository;
    private final DriveTypeRepository driveTypeRepository;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("🔥 ReferenceDataInitializer STARTED");

        if (countryRepository.count() > 0) {
            System.out.println("⛔ Reference data already exists");
            return;
        }

        // ==========================
        // GEO
        // ==========================

        Country kz = countryRepository.save(
                Country.builder()
                        .name("Kazakhstan")
                        .currencyCode("KZT")
                        .active(true)
                        .build()
        );

        Region almatyRegion = regionRepository.save(
                Region.builder()
                        .name("Almaty Region")
                        .country(kz)
                        .active(true)
                        .build()
        );

        City almaty = cityRepository.save(
                City.builder()
                        .name("Almaty")
                        .region(almatyRegion)
                        .active(true)
                        .build()
        );

        // ==========================
        // TRANSMISSIONS
        // ==========================

        Transmission automatic = transmissionRepository.save(
                Transmission.builder()
                        .name("Automatic")
                        .active(true)
                        .build()
        );

        Transmission manual = transmissionRepository.save(
                Transmission.builder()
                        .name("Manual")
                        .active(true)
                        .build()
        );

        // ==========================
        // DRIVE TYPES
        // ==========================

        DriveType awd = driveTypeRepository.save(
                DriveType.builder()
                        .name("AWD")
                        .active(true)
                        .build()
        );

        DriveType fwd = driveTypeRepository.save(
                DriveType.builder()
                        .name("FWD")
                        .active(true)
                        .build()
        );

        // ==========================
        // BMW X5 F15
        // ==========================

        Brand bmw = brandRepository.save(
                Brand.builder()
                        .name("BMW")
                        .active(true)
                        .build()
        );

        Model x5 = modelRepository.save(
                Model.builder()
                        .name("X5")
                        .brand(bmw)
                        .active(true)
                        .build()
        );

        Generation f15 = generationRepository.save(
                Generation.builder()
                        .name("F15")
                        .model(x5)
                        .yearFrom(2013)
                        .yearTo(2018)
                        .active(true)
                        .build()
        );

        Engine bmwDiesel = engineRepository.save(
                Engine.builder()
                        .name("3.0 Diesel")
                        .volume(3.0)
                        .fuelType("Diesel")
                        .generation(f15)
                        .active(true)
                        .build()
        );

        Engine bmwPetrol = engineRepository.save(
                Engine.builder()
                        .name("4.4 Petrol")
                        .volume(4.4)
                        .fuelType("Petrol")
                        .generation(f15)
                        .active(true)
                        .build()
        );

        // BMW engines → only automatic
        bmwDiesel.setTransmissions(Set.of(automatic));
        bmwPetrol.setTransmissions(Set.of(automatic));

        // ==========================
        // Toyota Camry XV70
        // ==========================

        Brand toyota = brandRepository.save(
                Brand.builder()
                        .name("Toyota")
                        .active(true)
                        .build()
        );

        Model camry = modelRepository.save(
                Model.builder()
                        .name("Camry")
                        .brand(toyota)
                        .active(true)
                        .build()
        );

        Generation xv70 = generationRepository.save(
                Generation.builder()
                        .name("XV70")
                        .model(camry)
                        .yearFrom(2018)
                        .yearTo(null)
                        .active(true)
                        .build()
        );

        Engine camryEngine = engineRepository.save(
                Engine.builder()
                        .name("2.5 Petrol")
                        .volume(2.5)
                        .fuelType("Petrol")
                        .generation(xv70)
                        .active(true)
                        .build()
        );

        camryEngine.setTransmissions(Set.of(automatic));

        System.out.println("✅ Reference data inserted successfully");
    }
}