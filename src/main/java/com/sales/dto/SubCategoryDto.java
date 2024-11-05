package com.sales.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryDto {
    Integer id;
    Integer categoryId;
    String subcategory;
    String icon;
    String unit;
}
