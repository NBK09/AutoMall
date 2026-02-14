package com.auto.mall.geo.repository;

import com.auto.mall.geo.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByName(String name);

    boolean existsByName(String name);
}