package com.auto.mall.geo.repository;

import com.auto.mall.geo.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findAllByOrderByNameAsc();
    Optional<City> findByName(String name);
}