package com.auto.mall.vehicle.generation.entity;

import com.auto.mall.vehicle.model.entity.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "generations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Generation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer yearFrom;
    private Integer yearTo;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;
}