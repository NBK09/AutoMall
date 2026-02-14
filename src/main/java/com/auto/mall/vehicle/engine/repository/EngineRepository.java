package com.auto.mall.vehicle.engine.repository;

import com.auto.mall.vehicle.engine.entity.Engine;
import com.auto.mall.vehicle.generation.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngineRepository extends JpaRepository<Engine, Long> {
    List<Engine> findByGenerationIdAndActiveTrueOrderByNameAsc(Long generationId);
    boolean existsByNameAndGeneration(String name, Generation generation);
}
