package com.sales.entities;

import javax.persistence.*;


@Table(name = "item_measurement_unit")
@Entity
public class ItemMeasurementUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "subcategoryId")
    Integer subcategoryId;

    @Column(name = "unit")
    String unit;


}
