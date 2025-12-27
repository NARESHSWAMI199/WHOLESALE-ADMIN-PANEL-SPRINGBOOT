package com.sales.wholesaler.services;


import com.sales.dto.StorePromotionDto;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WholesalePromotionsService extends WholesaleRepoContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(WholesalePromotionsService.class);

    public Map<String,Object> insertItemPromotion(StorePromotionDto storePromotionDto, User loggedUser) {
        safeLog.info(logger,"Starting insertItemPromotion method with storePromotionDto: {}, loggedUser: {}", storePromotionDto, loggedUser);
        Map<String,Object> response = new HashMap<>();
        Integer storeId = wholesaleStoreRepository.getStoreIdByUserId(loggedUser.getId());
        storePromotionDto.setStoreId(storeId);
        int isInserted = wholesaleHbPromotion.insertStorePromotions(storePromotionDto,loggedUser); // Create operation
        if(isInserted > 0){
            response.put(ConstantResponseKeys.MESSAGE,"Your item is going to promote.");
            response.put(ConstantResponseKeys.STATUS,200);
        }else {
            response.put(ConstantResponseKeys.MESSAGE,"Something went wrong during promote item. if your money was deducted contact to administrator");
            response.put(ConstantResponseKeys.STATUS,400);
        }
        safeLog.info(logger,"Completed insertItemPromotion method");
        return response;
    }


}
