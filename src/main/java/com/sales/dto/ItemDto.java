package com.sales.dto;


import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class ItemDto {
    private String name;
    private String wholesaleSlug;
    private float price;
    private float discount;
    private float rating;
    private String description;
    private String InStock;
    private  String slug;
    private String label;
    private  Float capacity;
    private String measureUnit;
    private String  avtar;
    private MultipartFile itemImage;
    private Integer storeId;
    private Integer categoryId = 0;
    private Integer subCategoryId = 0;
    private ItemCategory itemCategory;
    private ItemSubCategory itemSubCategory;
}
