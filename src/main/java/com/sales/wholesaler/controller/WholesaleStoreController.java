package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.dto.StoreDto;
import com.sales.entities.StoreCategory;
import com.sales.entities.StoreNotifications;
import com.sales.entities.StoreSubCategory;
import com.sales.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
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


    @Transactional
    @PostMapping(value = {"notifications"})
    public ResponseEntity<Page<StoreNotifications>> getAllStoreNotification(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        User logggedUser = (User) request.getAttribute("user");
        Page<StoreNotifications> storeNotifications = wholesaleStoreService.getAllStoreNotification(searchFilters,logggedUser);
        return new ResponseEntity<>(storeNotifications, HttpStatus.OK);
    }


    @Transactional
    @PostMapping(value = {"update/notifications"})
    public ResponseEntity<String> getAllStoreNotification(HttpServletRequest request, @RequestBody StoreDto storeDto) {
        User logggedUser = (User) request.getAttribute("user");
        wholesaleStoreService.updateSeen(storeDto.getSeenIds());
        return new ResponseEntity<>("success", HttpStatus.OK);
    }


    @GetMapping("category")
    public ResponseEntity<List<StoreCategory>> getAllStoreCategory() {
        List<StoreCategory> storeCategories = wholesaleStoreService.getAllStoreCategory();
        return new ResponseEntity<>(storeCategories, HttpStatus.OK);
    }


    @GetMapping("subcategory/{categoryId}")
    public ResponseEntity<List<StoreSubCategory>> getStoreSubCategory(@PathVariable(required = true) int categoryId) {
        List<StoreSubCategory> storeSubCategories = wholesaleStoreService.getAllStoreSubCategories(categoryId);
        return new ResponseEntity<>(storeSubCategories, HttpStatus.OK);
    }


}
