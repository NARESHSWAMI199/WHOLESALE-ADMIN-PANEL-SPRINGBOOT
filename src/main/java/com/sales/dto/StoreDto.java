package com.sales.dto;

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
}
