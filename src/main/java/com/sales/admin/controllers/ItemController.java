package com.sales.admin.controllers;

import com.sales.admin.repositories.ItemHbRepository;
import com.sales.dto.*;
import com.sales.entities.*;
import com.sales.utils.Utils;
import com.sales.utils.WriteExcel;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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


    @Autowired
    WriteExcel writeExcel;

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @PostMapping("/all")
    public ResponseEntity<Page<Item>> getAllItem(@RequestBody ItemSearchFields searchFilters) {
        logger.info("Fetching all items with filters: {}", searchFilters);
        Page<Item> alItems = itemService.getAllItems(searchFilters);
        return new ResponseEntity<>(alItems, HttpStatus.OK);
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable String slug) {
        logger.info("Fetching item details for slug: {}", slug);
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
        logger.info("Adding or updating item: {}", itemDto);
        User loggedUser = (User) request.getAttribute("user");
        String path = request.getRequestURI();
        System.err.println(itemDto.toString());
        Map<String,Object> responseObj = itemService.createOrUpdateItem(itemDto, loggedUser,path);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping(value = {"/importExcel/{wholesaleSlug}"})
    public ResponseEntity<Object> importItemsFromExcelSheet(HttpServletRequest request, @RequestParam("excelfile") MultipartFile excelSheet, @PathVariable("wholesaleSlug") String wholesaleSlug) {
        logger.info("Importing items from Excel sheet for wholesaleSlug: {}", wholesaleSlug);
        Map<String,Object> responseObj = new HashMap<>();
        try {
            if (excelSheet != null) {
                Map<String,List<String>> result = readExcel.getExcelDataInJsonFormat(excelSheet);
                User user = (User) request.getAttribute("user");
                Integer wholesaleId = storeService.getStoreIdByStoreSlug(wholesaleSlug);
                if (wholesaleId == null) throw new Exception("Wholesale was not found.");
                List<ItemHbRepository.ItemUpdateError> updateItemsError = itemService.updateItemsWithExcel(result, user.getId(), wholesaleId);
                if(updateItemsError.isEmpty()) {
                    responseObj.put("message", "Items successfully updated.");
                    responseObj.put("status", 200);
                    logger.info("Items successfully updated : {} ",updateItemsError);
                }else{
                    // Creating an Excel for which items are not updated
                    String [] headers = {"NAME","TOKEN","PRICE", "DISCOUNT","LABEL","CAPACITY","IN-STOCK","REASON"};
                    String fileName = writeExcel.writeNotUpdatedItemsExcel(updateItemsError, headers, wholesaleSlug);
                    responseObj.put("fileUrl", Utils.getHostUrl(request)+"/admin/item/notUpdated/"+wholesaleSlug+"/"+fileName);
                    responseObj.put("message", "Some items are not updated.");
                    responseObj.put("status", 201);
                    logger.info("Some items are not updated : {} ",updateItemsError);
                }

            } else {
                responseObj.put("message", "Please add a proper file.");
                responseObj.put("status", 400);
            }
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            logger.error("Facing Exception during updating or importing item from excel sheet  ; {}",e.getMessage());
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping(value = {"/exportExcel/{wholesaleSlug}"})
    public ResponseEntity<Object> exportItemsFromExcel(@PathVariable String wholesaleSlug, @RequestBody SearchFilters searchFilters) {
        logger.info("Exporting items to Excel for wholesaleSlug: {}", wholesaleSlug);
        Map<String,Object> responseObj = new HashMap<>();
        try {
            Store wholesale = storeService.getStoreDetails(wholesaleSlug);
            if (wholesale != null) {
                searchFilters.setStoreId(wholesale.getId());
                String filePath = itemService.createItemsExcelSheet(searchFilters,wholesaleSlug);
                Path path = Paths.get(filePath);
                Resource resource = new UrlResource(path.toUri());
                responseObj.put("message", "File successfully downloaded.");
                responseObj.put("status", 200);
                MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                return ResponseEntity.ok().contentType(mediaType).body(resource);
            } else {
                logger.info("wholeSlug : " + wholesaleSlug);
                responseObj.put("message", "Store not exist.");
                responseObj.put("status", 500);
            }
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            logger.error("Exception during export excel : {}",e.getMessage(),e);
        }
        logger.info("ENDED exportItemsFromExcel.");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteItemBySlug(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Deleting item with slug: {}", deleteDto);
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
        logger.info("Updating stock for item with slug: {}", params.get("slug"));
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
        logger.info("Updating status for item with statusDto: {}", statusDto);
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
        logger.info("Fetching image file: {} for slug: {}", filename, slug);
        Path path = Paths.get(filePath +slug+ "/"+filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }



    // ================= Item category


    @PostMapping("category")
    public ResponseEntity<List<ItemCategory>> getAllCategory(@RequestBody  SearchFilters searchFilters) {
        logger.info("Fetching all item categories with filters: {}", searchFilters);
        List<ItemCategory> itemCategories = itemService.getAllCategory(searchFilters);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

    @PostMapping(value = {"category/add","category/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemCategory(@RequestBody CategoryDto categoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Saving or updating item category: {}", categoryDto);
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
        logger.info("Deleting item category with id: {}", deleteDto);
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
        logger.info("Fetching item category with id: {}", categoryId);
        ItemCategory itemCategories = itemService.getItemCategoryById(categoryId);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }



    // ================= Item subcategory

    @PostMapping("subcategory")
    public ResponseEntity<List<ItemSubCategory>> getSubCategory(@RequestBody SearchFilters searchFilters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Fetching all item subcategories with filters: {}", searchFilters);
        List<ItemSubCategory> itemCategories = itemService.getAllItemsSubCategories(searchFilters);
        return new ResponseEntity<>(itemCategories, HttpStatus.OK);
    }

    @PostMapping(value = {"subcategory/add","subcategory/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemSubCategory(@RequestBody SubCategoryDto subCategoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Saving or updating item subcategory: {}", subCategoryDto);
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
        logger.info("Deleting item subcategory with id: {}", deleteDto);
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
        logger.info("Fetching all measuring units by subcategory");
        List<MeasurementUnit> itemMeasurementUnitList = itemService.getAllMeasurementUnit();
        return new ResponseEntity<>(itemMeasurementUnitList, HttpStatus.OK);
    }




    @Value("${excel.update.template}")
    String updateItemTemplate;


    @GetMapping(value = {"download/update/template"})
    public ResponseEntity<Object> downloadExcelUpdateTemplate() throws IOException {
        logger.info("Download excel sheet template for update items" );
        Path path = Paths.get(updateItemTemplate);
        Resource resource = new UrlResource(path.toUri());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")); // For .xlsx
        headers.setContentDispositionFormData("attachment", "update_item_template.xlsx");
        return new ResponseEntity<>(resource.getContentAsByteArray(), headers, org.springframework.http.HttpStatus.OK);
    }



    @Value("${excel.notUpdated.absolute}")
    String excelNotUpdateItemsFolderPath;


    @GetMapping(value = {"notUpdated/{wholesaleSlug}/{filename}"})
    public ResponseEntity<Object> downloadExcelUpdateTemplate(@PathVariable String wholesaleSlug ,@PathVariable String filename) throws IOException {
        logger.info("Download excel sheet template for not updated items : {}  : {}",excelNotUpdateItemsFolderPath,filename );
        Path path = Paths.get(excelNotUpdateItemsFolderPath+wholesaleSlug+ File.separator +filename);
        Resource resource = new UrlResource(path.toUri());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")); // For .xlsx
        headers.setContentDispositionFormData("attachment", "update_item_template.xlsx");
        return new ResponseEntity<>(resource.getContentAsByteArray(), headers, org.springframework.http.HttpStatus.OK);
    }






}
