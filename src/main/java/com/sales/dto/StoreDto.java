package com.sales.dto;

import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.StoreCategory;
import com.sales.entities.StoreSubCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class StoreDto extends AddressDto {
    private String storeEmail;
    private String userSlug;
    private String storeName;
    private Float rating = (float) 0;
    private String status;
    private String storePhone="";
    private String storeSlug;
    private String description ="";
    private String addressId;
    private String storeAvatar;
    private MultipartFile storePic;
    private List<Long> seenIds ;
    private Integer categoryId = 0;
    private Integer subCategoryId = 0;
    private StoreCategory storeCategory;
    private StoreSubCategory storeSubCategory;
}
