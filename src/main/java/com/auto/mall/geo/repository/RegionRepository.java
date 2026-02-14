package com.auto.mall.geo.repository;

import com.auto.mall.geo.entity.Country;
import com.auto.mall.geo.entity.Region;
import com.auto.mall.vehicle.engine.entity.Engine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByName(String name);

    boolean existsByName(String name);

    List<Region> findByCountryIdAndActiveTrue (Long countryId);
}