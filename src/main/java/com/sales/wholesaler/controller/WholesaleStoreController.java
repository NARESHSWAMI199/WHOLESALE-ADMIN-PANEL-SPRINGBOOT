package com.sales.wholesaler.controller;


import com.sales.dto.StoreDto;
import com.sales.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesale/store")
public class WholesaleStoreController extends WholesaleServiceContainer{


    @Transactional
    @PostMapping(value = {"/update"})
    public ResponseEntity<Map<String, Object>> updateAuth(HttpServletRequest request, @ModelAttribute StoreDto storeDto) {
        Map<String,Object> responseObj = new HashMap<>();
        try {
            User logggedUser = (User) request.getAttribute("user");
            responseObj = wholesaleStoreService.updateStoreBySlug(storeDto, logggedUser);
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }


}
