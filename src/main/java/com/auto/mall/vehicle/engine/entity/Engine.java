package com.auto.mall.vehicle.engine.entity;

import com.auto.mall.vehicle.generation.entity.Generation;
import com.auto.mall.vehicle.transmission.entity.Transmission;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "engines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double volume;

    private String fuelType;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "generation_id")
    private Generation generation;

    // 🔥 ВОТ ЭТОГО У ТЕБЯ НЕТ
    @ManyToMany
    @JoinTable(
            name = "engine_transmissions",
            joinColumns = @JoinColumn(name = "engine_id"),
            inverseJoinColumns = @JoinColumn(name = "transmission_id")
    )
    @Builder.Default
    private Set<Transmission> transmissions = new HashSet<>();
}