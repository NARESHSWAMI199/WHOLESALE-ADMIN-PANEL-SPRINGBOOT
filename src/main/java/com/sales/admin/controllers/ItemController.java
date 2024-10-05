package com.sales.admin.controllers;

import com.sales.dto.ItemDto;
import com.sales.dto.SearchFilters;
import com.sales.dto.StatusDto;
import com.sales.entities.Item;
import com.sales.entities.Store;
import com.sales.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = {"admin/item"})
public class ItemController extends ServiceContainer {
    @PostMapping("/all")
    public ResponseEntity<Page<Item>> getAllItem(@RequestBody SearchFilters searchFilters) {
        Page<Item> alItems = itemService.getAllItems(searchFilters);
        return new ResponseEntity<>(alItems, HttpStatus.OK);
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable String slug) {
        Map<String, Object> responseObj = new HashMap<>();
        Item alItems = itemService.findItemBySLug(slug);
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
            User logggedUser = (User) request.getAttribute("user");
            responseObj = itemService.createOrUpdateItem(itemDto, logggedUser);
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            e.printStackTrace();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping(value = {"/importExcel/{wholesaleSlug}"})
    public ResponseEntity<Map<String, Object>> importItemsFromExcelSheet(HttpServletRequest request, @RequestParam("excelfile") MultipartFile excelSheet, @PathVariable("wholesaleSlug") String wholesaleSlug) {
        Map responseObj = new HashMap();
        try {
            if (excelSheet != null) {
                Map result = readExcel.getExcelDataInJsonFormat(excelSheet);
                User user = (User) request.getAttribute("user");
                Store wholesale = storeService.getStoreDetails(wholesaleSlug);
                if (wholesale != null) {itemService.insertAllItems(result, user.getId(), wholesale.getId());}
                else { throw  new Exception("Wholesale was not found.");}
                responseObj.put("res", result);
                responseObj.put("message", "Items successfully inserted.");
                responseObj.put("status", 200);

            } else {
                responseObj.put("message", "Please add a proper file.");
                responseObj.put("status", 400);
            }
//            User logggedUser = (User) request.getAttribute("user");
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            e.printStackTrace();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping(value = {"/exportExcel/{wholesaleSlug}"})
    public ResponseEntity<Map<String, Object>> exportItemsFromExcel(@PathVariable String wholesaleSlug, @RequestBody SearchFilters searchFilters) {
        logger.info("STARTED exportItemsFromExcel.");
        Map responseObj = new HashMap();
        try {
            Store wholesale = storeService.getStoreDetails(wholesaleSlug);
            if (wholesale != null) {
                searchFilters.setStoreId(wholesale.getId());
                Map result = itemService.createItemsExcelSheet(searchFilters);
                responseObj.put("res", result);
                responseObj.put("status", 200);
            } else {
                logger.info("wholeSlug : " + wholesaleSlug);
                responseObj.put("message", "Store not exist.");
                responseObj.put("status", 500);
            }
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            e.printStackTrace();
        }
        logger.info("ENDED exportItemsFromExcel.");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @GetMapping("/delete/{slug}")
    public ResponseEntity<Map<String, Object>> deleteItemBySlug(@PathVariable String slug) {
        Map responseObj = new HashMap();
        int isUpdated = itemService.deleteItem(slug);
        if (isUpdated > 0) {
            responseObj.put("message", "Item has been successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/stock")
    public ResponseEntity<Map<String, Object>> updateItemStock(@RequestBody Map<String, String> params) {
        Map responseObj = new HashMap();
        int isUpdated = itemService.updateStock(params.get("stock"), params.get("slug"));
        if (isUpdated > 0) {
            responseObj.put("message", "Item's stock has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is not item to update.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> updateItemStatus(@RequestBody StatusDto statusDto) {
        Map responseObj = new HashMap();
        int isUpdated = itemService.updateStatusBySlug(statusDto);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's status has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @Value("${item.get}")
    String filePath;

    @GetMapping("/image/{slug}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable(required = true) String filename, @PathVariable("slug") String slug ) throws Exception {
        Path path = Paths.get(filePath +slug+ "/"+filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }

}
