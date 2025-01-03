package com.sales.wholesaler.controller;


import com.sales.dto.AddressDto;
import com.sales.dto.SearchFilters;
import com.sales.dto.StoreDto;
import com.sales.entities.*;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("wholesale/store")
public class WholesaleStoreController extends WholesaleServiceContainer{


    @Transactional
    @PostMapping(value = {"/update"})
    public ResponseEntity<Map<String, Object>> updateStore(HttpServletRequest request, @ModelAttribute StoreDto storeDto) {
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
        User loggedUser = (User) request.getAttribute("user");
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


    @PostMapping("add")
    @Transactional
    public ResponseEntity<Map<String,Object>> addNewStore(HttpServletRequest request,@RequestBody StoreDto storeDto) {
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Store isInserted = wholesaleStoreService.createStore(storeDto,loggedUser);
        if(isInserted.getId() > 0){
            result.put("message","Store created successfully. Welcome in Swami Sales");
            result.put("status",200);
        }else{
            result.put("message","Something went wrong");
            result.put("status",400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


}
