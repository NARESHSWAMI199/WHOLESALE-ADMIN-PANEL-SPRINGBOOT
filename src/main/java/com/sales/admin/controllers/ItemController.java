package com.sales.admin.controllers;

import com.sales.dto.*;
import com.sales.entities.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"admin/item"})
public class ItemController extends ServiceContainer {
    @PostMapping("/all")
    public ResponseEntity<Page<Item>> getAllItem(@RequestBody ItemSearchFields searchFilters) {
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


    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(example = """
                {
                    "slug" : "(only during update) string",
                    "name": "string",
                    "wholesaleSlug": "string",
                    "price": 0,
                    "discount": 0,
                    "rating": 0.0,
                    "description": "string",
                    "capacity": 1,
                    "categoryId": 0,
                    "subCategoryId": 0,
                    "inStock": "Y|N",
                    "label": "N|O",
                    "newItemImages" : "[Multipart images list]",
                    "previousItemImages" , "during update only | string.jpeg,string.png"
                }
            """)
            ))

    @PostMapping(value = {"/add", "/update"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addOrUpdateItems(HttpServletRequest request, @ModelAttribute ItemDto itemDto) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        User loggedUser = (User) request.getAttribute("user");
        String path = request.getRequestURI();
        System.err.println(itemDto.toString());
        Map<String,Object> responseObj = itemService.createOrUpdateItem(itemDto, loggedUser,path);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping(value = {"/importExcel/{wholesaleSlug}"})
    public ResponseEntity<Map<String, Object>> importItemsFromExcelSheet(HttpServletRequest request, @RequestParam("excelfile") MultipartFile excelSheet, @PathVariable("wholesaleSlug") String wholesaleSlug) {
        Map<String,Object> responseObj = new HashMap<>();
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
//            User loggedUser = (User) request.getAttribute("user");
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
        Map<String,Object> responseObj = new HashMap<>();
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


    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteItemBySlug(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = itemService.deleteItem(deleteDto,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Item has been successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No item found to delete.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }





    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(example = """
            {
               "stock" : "Y|N",
               "slug" : "string"
            }
            """)
    ))
    @PostMapping("/stock")
    public ResponseEntity<Map<String, Object>> updateItemStock(@RequestBody Map<String, String> params) {
        Map<String,Object> responseObj = new HashMap<>();
        int isUpdated = itemService.updateStock(params.get("stock"), params.get("slug"));
        if (isUpdated > 0) {
            responseObj.put("message", "Item's stock has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No item found to update.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> updateItemStatus(HttpServletRequest request ,@RequestBody StatusDto statusDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = itemService.updateStatusBySlug(statusDto,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's status has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No item found to update.");
            responseObj.put("status", 404);
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



    // ================= Item category


    @PostMapping("category")
    public ResponseEntity<List<ItemCategory>> getAllCategory(@RequestBody  SearchFilters searchFilters) {
        List<ItemCategory> itemCategories = itemService.getAllCategory(searchFilters);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

    @PostMapping(value = {"category/add","category/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemCategory(@RequestBody CategoryDto categoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> result = new HashMap<>();
        ItemCategory updatedItemCategory = itemService.saveOrUpdateItemCategory(categoryDto);
        if(updatedItemCategory != null) {
            result.put("res",updatedItemCategory); // during update and inserted for both
            if(categoryDto.getId() != null && categoryDto.getId() != 0) {
                result.put("message", "Category successfully updated.");
                result.put("status", 200);
            }else {
                result.put("message", "Category successfully inserted.");
                result.put("status", 201);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("category/delete")
    public ResponseEntity<Map<String,Object>> deleteItemCategoryById(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = itemService.deleteItemCategory(deleteDto,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's category delete successfully.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No category to found.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @GetMapping("category/{categoryId}")
    public ResponseEntity<ItemCategory> getAllCategory(@PathVariable Integer categoryId) {
        ItemCategory itemCategories = itemService.getItemCategoryById(categoryId);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }



    // ================= Item subcategory

    @PostMapping("subcategory")
    public ResponseEntity<List<ItemSubCategory>> getSubCategory(@RequestBody SearchFilters searchFilters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<ItemSubCategory> itemCategories = itemService.getAllItemsSubCategories(searchFilters);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

    @PostMapping(value = {"subcategory/add","subcategory/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemSubCategory(@RequestBody SubCategoryDto subCategoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> result = new HashMap<>();
        ItemSubCategory updateItemSubCategory = itemService.saveOrUpdateItemSubCategory(subCategoryDto);
        if(updateItemSubCategory != null) {
            result.put("res",updateItemSubCategory); // during update and inserted for both
            if(subCategoryDto.getId() != null) {
                result.put("message", "Category successfully updated.");
                result.put("status", 200);
            }else {
                result.put("message", "Category successfully inserted.");
                result.put("status", 201);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("subcategory/delete")
    public ResponseEntity<Map<String,Object>> deleteItemSubCategoryById(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = itemService.deleteItemSubCategory(deleteDto,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's subcategory deleted successfully");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No subcategory found to delete.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @GetMapping("units")
    public ResponseEntity<List<MeasurementUnit>> getALlMeasuringUnitsBySubcategory() {
        List<MeasurementUnit> itemMeasurementUnitList = itemService.getAllMeasurementUnit();
        return new ResponseEntity<>(itemMeasurementUnitList, HttpStatus.OK);
    }




}
