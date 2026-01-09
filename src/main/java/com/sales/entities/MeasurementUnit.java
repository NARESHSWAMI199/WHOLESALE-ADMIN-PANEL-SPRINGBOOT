package com.sales.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Table(name = "item_measurement_units")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementUnit implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    int id;

    @Column(name = "unit")
    String unit;
}
