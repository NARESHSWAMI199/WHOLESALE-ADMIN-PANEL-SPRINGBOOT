package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "item_subcategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "category_id")
    Integer categoryId;

    @Column(name = "subcategory")
    String subcategory;

    @Column(name = "icon")
    String icon;


}
