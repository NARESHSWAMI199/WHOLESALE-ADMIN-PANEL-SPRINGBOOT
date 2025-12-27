package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.dto.StoreDto;
import com.sales.entities.*;
import com.sales.global.ConstantResponseKeys;
import com.sales.utils.Utils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wholesale/store")
@RequiredArgsConstructor
public class WholesaleStoreController extends WholesaleServiceContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(WholesaleStoreController.class);

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema =
    @Schema(example = """
                {
                  "storeSlug" : "string",
                  "storeEmail": "string",
                  "storeName": "string",
                  "storePhone": "string",
                  "description": "string",
                  "storePic": "string",
                  "street": "string",
                  "zipCode": "string",
                  "city": 0,
                  "state": 0,
                  "categoryId" : "0",
                  "subcategoryId" : "0"
                }
                """
    )))
    @Transactional
    @PostMapping(value = {"/update"})
    public ResponseEntity<Map<String, Object>> updateStore(HttpServletRequest request, @ModelAttribute StoreDto storeDto) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        safeLog.info(logger,"Starting updateStore method");
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> responseObj = wholesaleStoreService.updateStoreBySlug(storeDto, loggedUser);
        safeLog.info(logger,"Completed updateStore method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get(ConstantResponseKeys.STATUS)));
    }


    @Transactional
    @PostMapping(value = {"notifications"})
    public ResponseEntity<Page<StoreNotifications>> getAllStoreNotification(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        safeLog.info(logger,"Starting getAllStoreNotification method");
        User loggedUser = (User) request.getAttribute("user");
        Page<StoreNotifications> storeNotifications = wholesaleStoreService.getAllStoreNotification(searchFilters,loggedUser);
        safeLog.info(logger,"Completed getAllStoreNotification method");
        return new ResponseEntity<>(storeNotifications, HttpStatus.OK);
    }



    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(
            example = """
                    "seenIds": [
                        "the list of seen notification ids"
                      ],
                    """
    )))
    @Transactional
    @PostMapping(value = {"update/notifications"})
    public ResponseEntity<String> getAllStoreNotification(@RequestBody StoreDto storeDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        safeLog.info(logger,"Starting getAllStoreNotification method");
        wholesaleStoreService.updateSeen(storeDto);
        safeLog.info(logger,"Completed getAllStoreNotification method");
        return new ResponseEntity<>(ConstantResponseKeys.SUCCESS, HttpStatus.valueOf(200));
    }


    @GetMapping("category")
    public ResponseEntity<List<StoreCategory>> getAllStoreCategory() {
        safeLog.info(logger,"Starting getAllStoreCategory method");
        List<StoreCategory> storeCategories = wholesaleStoreService.getAllStoreCategory();
        safeLog.info(logger,"Completed getAllStoreCategory method");
        return new ResponseEntity<>(storeCategories, HttpStatus.OK);
    }


    @GetMapping("subcategory/{categoryId}")
    public ResponseEntity<List<StoreSubCategory>> getStoreSubCategory(@PathVariable(required = true) int categoryId) {
        safeLog.info(logger,"Starting getStoreSubCategory method");
        List<StoreSubCategory> storeSubCategories = wholesaleStoreService.getAllStoreSubCategories(categoryId);
        safeLog.info(logger,"Completed getStoreSubCategory method");
        return new ResponseEntity<>(storeSubCategories, HttpStatus.OK);
    }



    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema =
        @Schema(example = """
                {
                  "storeEmail": "string",
                  "storeName": "string",
                  "storePhone": "string",
                  "description": "string",
                  "storePic": "string",
                  "street": "string",
                  "zipCode": "string",
                  "city": 0,
                  "state": 0,
                  "categoryId" : "0",
                  "subcategoryId" : "0"
                }
                """
    )))
    @PostMapping(value = "add",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<Map<String,Object>> addNewStore(HttpServletRequest request,@ModelAttribute StoreDto storeDto) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        safeLog.info(logger,"Starting addNewStore method");
        Map<String,Object> result = new HashMap<>();
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Store isInserted = wholesaleStoreService.createStore(storeDto,loggedUser);
        if(isInserted.getId() > 0){
            result.put(ConstantResponseKeys.MESSAGE,"Store created successfully. Welcome in Swami Sales");
            result.put(ConstantResponseKeys.STATUS,200);
        }else{
            result.put(ConstantResponseKeys.MESSAGE,"Something went wrong");
            result.put(ConstantResponseKeys.STATUS,400);
        }
        safeLog.info(logger,"Completed addNewStore method");
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


}
