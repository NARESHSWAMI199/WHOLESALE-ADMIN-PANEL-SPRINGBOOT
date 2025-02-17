package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.dto.StoreDto;
import com.sales.entities.*;
import com.sales.utils.Utils;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
public class WholesaleStoreController extends WholesaleServiceContainer {

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
        logger.info("Starting updateStore method");
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> responseObj = wholesaleStoreService.updateStoreBySlug(storeDto, loggedUser);
        logger.info("Completed updateStore method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @Transactional
    @PostMapping(value = {"notifications"})
    public ResponseEntity<Page<StoreNotifications>> getAllStoreNotification(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        logger.info("Starting getAllStoreNotification method");
        User loggedUser = (User) request.getAttribute("user");
        Page<StoreNotifications> storeNotifications = wholesaleStoreService.getAllStoreNotification(searchFilters,loggedUser);
        logger.info("Completed getAllStoreNotification method");
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
        logger.info("Starting getAllStoreNotification method");
        wholesaleStoreService.updateSeen(storeDto);
        logger.info("Completed getAllStoreNotification method");
        return new ResponseEntity<>("success", HttpStatus.valueOf(200));
    }


    @GetMapping("category")
    public ResponseEntity<List<StoreCategory>> getAllStoreCategory() {
        logger.info("Starting getAllStoreCategory method");
        List<StoreCategory> storeCategories = wholesaleStoreService.getAllStoreCategory();
        logger.info("Completed getAllStoreCategory method");
        return new ResponseEntity<>(storeCategories, HttpStatus.OK);
    }


    @GetMapping("subcategory/{categoryId}")
    public ResponseEntity<List<StoreSubCategory>> getStoreSubCategory(@PathVariable(required = true) int categoryId) {
        logger.info("Starting getStoreSubCategory method");
        List<StoreSubCategory> storeSubCategories = wholesaleStoreService.getAllStoreSubCategories(categoryId);
        logger.info("Completed getStoreSubCategory method");
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
        logger.info("Starting addNewStore method");
        Map<String,Object> result = new HashMap<>();
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Store isInserted = wholesaleStoreService.createStore(storeDto,loggedUser);
        if(isInserted.getId() > 0){
            result.put("message","Store created successfully. Welcome in Swami Sales");
            result.put("status",200);
        }else{
            result.put("message","Something went wrong");
            result.put("status",400);
        }
        logger.info("Completed addNewStore method");
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


}
