package com.sales.wholesaler.controller;


import com.sales.claims.AuthUser;
import com.sales.claims.SalesUser;
import com.sales.dto.StorePromotionDto;
import com.sales.wholesaler.services.WholesalePromotionsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("wholesale/promotions")
@RequiredArgsConstructor
public class WholesalePromotionController  {

    private final WholesalePromotionsService wholesalePromotionsService;
    private static final Logger logger = LoggerFactory.getLogger(WholesalePromotionController.class);

    @PostMapping("/")
    public ResponseEntity<Map<String,Object>> insertPromotedItem(Authentication authentication, HttpServletRequest request, @ModelAttribute StorePromotionDto storePromotionDto){
        logger.debug("Starting insertPromotedItem method");
        AuthUser loggedUser = (SalesUser) authentication.getPrincipal();
        Map<String,Object> response = wholesalePromotionsService.insertItemPromotion(storePromotionDto,loggedUser);
        logger.debug("Completed insertPromotedItem method");
        return new ResponseEntity<>(response,HttpStatus.valueOf((Integer) response.get("status")));
    }


}
