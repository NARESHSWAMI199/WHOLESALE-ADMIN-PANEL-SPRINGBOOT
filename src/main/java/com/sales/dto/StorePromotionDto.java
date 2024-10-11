package com.sales.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StorePromotionDto {
    Integer id;
    String bannerImage = "test.png";
    String promotionType;
    Integer storeId;
    Integer itemId;
    String priority;
    Long priorityHours;
    Integer maxRepeat;
    Integer stateId;
    Integer cityId;
    Long startDate;
    Long expiryDate;
    MultipartFile promotionFile;
}
