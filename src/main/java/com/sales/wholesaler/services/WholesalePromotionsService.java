package com.sales.wholesaler.services;


import com.sales.dto.StorePromotionDto;
import com.sales.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WholesalePromotionsService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesalePromotionsService.class);

    public Map<String,Object> insertItemPromotion(StorePromotionDto storePromotionDto, User loggedUser) {
        logger.info("Starting insertItemPromotion method with storePromotionDto: {}, loggedUser: {}", storePromotionDto, loggedUser);
        Map<String,Object> response = new HashMap<>();
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(loggedUser.getId());
        storePromotionDto.setStoreId(storeId);
        int isInserted = wholesaleHbPromotion.insertStorePromotions(storePromotionDto,loggedUser); // Create operation
        if(isInserted > 0){
            response.put("message","Your item is going to promote.");
            response.put("status",200);
        }else {
            response.put("message","Something went wrong during promote item. if your money was deducted contact to administrator");
            response.put("status",400);
        }
        logger.info("Completed insertItemPromotion method");
        return response;
    }


}
