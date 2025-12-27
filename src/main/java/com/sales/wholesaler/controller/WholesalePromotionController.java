package com.sales.wholesaler.controller;


import com.sales.dto.StorePromotionDto;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("wholesale/promotions")
@RequiredArgsConstructor
public class WholesalePromotionController extends WholesaleServiceContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(WholesalePromotionController.class);

    @PostMapping("/")
    public ResponseEntity<Map<String,Object>> insertPromotedItem(HttpServletRequest request, @ModelAttribute StorePromotionDto storePromotionDto){
        safeLog.info(logger,"Starting insertPromotedItem method");
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> response = wholesalePromotionsService.insertItemPromotion(storePromotionDto,loggedUser);
        safeLog.info(logger,"Completed insertPromotedItem method");
        return new ResponseEntity<>(response,HttpStatus.valueOf((Integer) response.get("status")));
    }


}
