package com.auto.mall.vehicle.generation.repository;

import com.auto.mall.vehicle.generation.entity.Generation;
import com.auto.mall.vehicle.model.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    List<Generation> findByModelIdAndActiveTrueOrderByNameAsc(Long modelId);
    Optional<Generation> findByNameAndModel(String name, Model model);
}
