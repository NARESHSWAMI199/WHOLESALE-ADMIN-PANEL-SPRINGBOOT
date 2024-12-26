package com.sales.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Table(name = "item_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_deleted !='Y'")
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "slug")
    String slug = UUID.randomUUID().toString();

    @Column(name="category")
    String category;

    @Column(name = "icon")
    String icon;

    @Column(name = "is_deleted")
    String isDeleted = "N";

}
