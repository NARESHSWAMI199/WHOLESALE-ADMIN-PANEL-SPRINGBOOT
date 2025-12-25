package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;

@Entity(name = "ItemSubCategory")
@Table(name = "item_subcategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_deleted !='Y'")
public class ItemSubCategory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "slug" )
    String slug;

    @Column(name = "category_id")
    Integer categoryId;

    @Column(name = "subcategory")
    String subcategory;

    @Column(name = "unit")
    String unit;

    @Column(name = "icon")
    String icon;

    @Column(name = "updated_at")
    Long updatedAt;

    @Column(name = "is_deleted")
    String isDeleted = "N";





}
