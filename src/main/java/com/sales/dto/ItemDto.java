package com.sales.dto;


import com.sales.entities.ItemCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
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
    private String  avtar;
    private MultipartFile itemImage;
    private Integer storeId;
    private Integer categoryId = 0;
    private ItemCategory itemCategory;
}
