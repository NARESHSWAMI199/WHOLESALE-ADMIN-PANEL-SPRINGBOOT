package com.sales.entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Entity
@Table(name = "address")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name = "slug")
    String slug;
    @Column(name = "street")
    String street;
    @Column(name = "zip_code")
    String zipCode;
    @Column(name = "city")
    Integer city;
    @Column(name = "state")
    Integer state;
    @Column(name = "latitude")
    Float latitude;
    @Column(name = "altitude")
    Float altitude;
/**-------------> COMMON COLUMNS ---------------------*/
    @Column(name = "created_at")
    Long createdAt;
    @Column(name = "created_by")
    Integer createdBy;
    @Column(name = "updated_at")
    Long updatedAt;
    @Column(name = "updated_by")
    Integer updatedBy;
/**-------------! COMMON COLUMNS ---------------------*/

}
