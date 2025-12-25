package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "store_subcategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_deleted !='Y'")
public class StoreSubCategory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "slug")
    String slug = UUID.randomUUID().toString();

    @Column(name = "category_id")
    Integer categoryId;

    @Column(name = "subcategory")
    String subcategory;

    @Column(name = "icon")
    String icon;

    @Column(name = "updated_at")
    Long updatedAt;


    @Column(name = "is_deleted")
    String isDeleted ="N";


}
