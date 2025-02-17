package com.sales.wholesaler.controller;

import com.sales.dto.DeleteDto;
import com.sales.dto.ItemDto;
import com.sales.dto.ItemSearchFields;
import com.sales.entities.Item;
import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.User;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"wholesale/item"})
public class WholesaleItemController extends WholesaleServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemController.class);

    @PostMapping("/all")
    public ResponseEntity<Page<Item>> getAllItem(HttpServletRequest request,@RequestBody ItemSearchFields searchFilters) {
        logger.info("Starting getAllItem method");
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        Page<Item> alItems = wholesaleItemService.getAllItems(searchFilters,storeId);
        logger.info("Completed getAllItem method");
        return new ResponseEntity<>(alItems, HttpStatus.OK);
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable String slug) {
        logger.info("Starting getItem method");
        Map<String, Object> responseObj = new HashMap<>();
        Item alItems = wholesaleItemService.findItemBySLug(slug);
        if (alItems != null) {
            responseObj.put("message", "success");
            responseObj.put("res", alItems);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "Item Not Found");
            responseObj.put("status", 404);
        }
        logger.info("Completed getItem method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(
            example = """
                    {
                      "name": "string",
                      "price": 0,
                      "discount": 0,
                      "description": "string",
                      "label": "string",
                      "capacity": 0,
                      "itemImage": "string",
                      "storeId": 0,
                      "categoryId": 0,
                      "subCategoryId": 0,
                      "inStock" : "Y|N",
                       "newItemImages": [
                          "image"
                        ]
                    }
                    """
    )))
    @PostMapping(value = {"/add", "/update"})
    public ResponseEntity<Map<String, Object>> addOrUpdateItems(HttpServletRequest request, @ModelAttribute ItemDto itemDto) throws Exception {
        logger.info("Starting addOrUpdateItems method");
        User loggedUser = (User) request.getAttribute("user");
        String path = request.getRequestURI();
        Map responseObj = wholesaleItemService.createOrUpdateItem(itemDto, loggedUser,path);
        logger.info("Completed addOrUpdateItems method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String,Object>> deleteItemBySlug(HttpServletRequest request, @RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting deleteItemBySlug method");
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        int isUpdated = wholesaleItemService.deleteItem(deleteDto,storeId);
        if (isUpdated > 0) {
            responseObj.put("message", "Item has been successfully deleted.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "No item found to delete.");
            responseObj.put("status", 404);
        }
        logger.info("Completed deleteItemBySlug method");
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(
            example = """
                    {
                       "slug" : "string (item slug)",
                       "stock" : "Y|N"
                    }
                    """
    )))
    @PostMapping("/stock")
    public ResponseEntity<Map<String,Object>> updateItemStock (HttpServletRequest request,@RequestBody Map<String,String> params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Starting updateItemStock method");
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        int isUpdated = wholesaleItemService.updateStock(params,storeId);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's stock has been successfully updated.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "No item found to update.");
            responseObj.put("status", 404);
        }
        logger.info("Completed updateItemStock method");
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @GetMapping("category")
    public ResponseEntity<List<ItemCategory>> getAllCategory() {
        logger.info("Starting getAllCategory method");
        List<ItemCategory> itemCategories = wholesaleItemService.getAllCategory();
        logger.info("Completed getAllCategory method");
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

    @GetMapping("subcategory/{categoryId}")
    public ResponseEntity<List<ItemSubCategory>> getSubCategory(@PathVariable(required = true) int categoryId) {
        logger.info("Starting getSubCategory method");
        List<ItemSubCategory> itemCategories = wholesaleItemService.getAllItemsSubCategories(categoryId);
        logger.info("Completed getSubCategory method");
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

}
