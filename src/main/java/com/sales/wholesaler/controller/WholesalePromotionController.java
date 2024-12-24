package com.sales.wholesaler.controller;


import com.sales.dto.StorePromotionDto;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("wholesale/promotions")
public class WholesalePromotionController extends WholesaleServiceContainer {


    @PostMapping("/")
    public ResponseEntity<Map<String,Object>> insertPromotedItem(HttpServletRequest request, @ModelAttribute StorePromotionDto storePromotionDto){
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> response = wholesalePromotionsService.insertItemPromotion(storePromotionDto,loggedUser);
        return new ResponseEntity<>(response,HttpStatus.valueOf((Integer) response.get("status")));
    }


}
