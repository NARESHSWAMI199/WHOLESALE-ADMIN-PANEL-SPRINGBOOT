package com.sales.wholesaler.controller;

import com.sales.dto.ItemDto;
import com.sales.dto.ItemSearchFields;
import com.sales.entities.Item;
import com.sales.entities.ItemCategory;
import com.sales.entities.ItemSubCategory;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"wholesale/item"})
public class WholesaleItemController extends WholesaleServiceContainer {
    @PostMapping("/all")
    public ResponseEntity<Page<Item>> getAllItem(HttpServletRequest request,@RequestBody ItemSearchFields searchFilters) {
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        Page<Item> alItems = wholesaleItemService.getAllItems(searchFilters,storeId);
        return new ResponseEntity<>(alItems, HttpStatus.OK);
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable String slug) {
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
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @PostMapping(value = {"/add", "/update"})
    public ResponseEntity<Map<String, Object>> addOrUpdateItems(HttpServletRequest request, @ModelAttribute ItemDto itemDto) {
        Map responseObj = new HashMap();
        try {
            User loggedUser = (User) request.getAttribute("user");
            responseObj = wholesaleItemService.createOrUpdateItem(itemDto, loggedUser);
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            e.printStackTrace();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }




    @GetMapping("/delete/{slug}")
    public ResponseEntity<Map<String,Object>> deleteItemBySlug(HttpServletRequest request,@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        int isUpdated = wholesaleItemService.deleteItem(slug,storeId);
        if (isUpdated > 0) {
            responseObj.put("message", "Item has been successfully deleted.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/stock")
    public ResponseEntity<Map<String,Object>> updateItemStock (HttpServletRequest request,@RequestBody Map<String,String> params) {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        int isUpdated = wholesaleItemService.updateStock(params.get("stock"),params.get("slug"),storeId);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's stock has been successfully updated.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "There is not item to update.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @GetMapping("category")
    public ResponseEntity<List<ItemCategory>> getAllCategory() {
        List<ItemCategory> itemCategories = wholesaleItemService.getAllCategory();
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }


    @GetMapping("subcategory/{categoryId}")
    public ResponseEntity<List<ItemSubCategory>> getSubCategory(@PathVariable(required = true) int categoryId) {
        List<ItemSubCategory> itemCategories = wholesaleItemService.getAllItemsSubCategories(categoryId);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

}
