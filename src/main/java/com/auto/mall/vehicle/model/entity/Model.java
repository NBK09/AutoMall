package com.auto.mall.vehicle.model.entity;

import com.auto.mall.vehicle.brand.entity.Brand;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "models")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
