package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "item_subcategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted !='Y'")
public class ItemSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "slug" )
    String slug = UUID.randomUUID().toString();;

    @Column(name = "category_id")
    Integer categoryId;

    @Column(name = "subcategory")
    String subcategory;

    @Column(name = "icon")
    String icon;

    @Column(name = "updated_at")
    Long updatedAt;

    @Column(name = "is_deleted")
    String isDeleted = "N";





}
