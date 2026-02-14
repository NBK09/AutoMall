package com.auto.mall.vehicle.transmission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transmissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean active;
}
