package com.sales.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "measurement_unit")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementUnit {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "unit")
    String unit;
}
