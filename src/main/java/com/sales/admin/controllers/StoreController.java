package com.sales.admin.controllers;

import com.sales.dto.*;
import com.sales.entities.Store;
import com.sales.entities.StoreCategory;
import com.sales.entities.StoreSubCategory;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
@RequestMapping("admin/store")
public class StoreController extends ServiceContainer{

    @PostMapping("/all")
    public ResponseEntity<Page<Store>> getAllStore(@RequestBody SearchFilters searchFilters){
        Page<Store> storePage =  storeService.getAllStore(searchFilters);
        return new ResponseEntity<>(storePage, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("delete")
    public ResponseEntity<Map<String,Object>> deleteStore(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isUpdated = storeService.deleteStoreBySlug(deleteDto,loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "Store has been successfully deleted.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "No store found to delete");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String,Object>> getStoreDetailBySlug(@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        Store store = storeService.getStoreDetails(slug);
        if (store!= null){
            responseObj.put("res", store);
            responseObj.put("status", 200);
        }else {
            responseObj.put("message", "No store found.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @GetMapping("/detailbyuser/{userSLug}")
    public ResponseEntity<Map<String,Object>> getUserDetailByUserSlug(@PathVariable("userSLug") String slug) throws Exception {
        Map<String,Object> responseObj = new HashMap<>();
        Store store = storeService.getStoreByUserSlug(slug);
        if (store!= null){
            responseObj.put("res", store);
            responseObj.put("status", 200);
        }else {
            responseObj.put("message", "No record found.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }




    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            schema = @Schema(example = """
                {
                    "storeSlug" : "string (during update only)",
                    "userSlug" : "string (during create only)",
                    "storeName" : "string",
                    "storeEmail" : "string",
                    "description" : "string",
                    "categoryId" : 0,
                    "subCategoryId"  : 0,
                    "storePhone" : "string",
                    "zipCode" : "string(6 digit)",
                    "city" : "cityId",
                    "state" : "stateId",
                    "street" : "string"
                }
                """)
    ))
    @Transactional
    @PostMapping(value = {"/add","/update"})
    public ResponseEntity<Map<String,Object>> addStoreOrUpdateStore(HttpServletRequest request,  @ModelAttribute StoreDto storeDto) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        User loggedUser = (User) request.getAttribute("user");
        String path = request.getRequestURI().toLowerCase();
        Map<String,Object> responseObj = storeService.createOrUpdateStore(storeDto,loggedUser,path);
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    /** currently we do not use it for upload store image */
    @Transactional
    @PostMapping("profile/{slug}")
    public ResponseEntity<Map<String,Object>> uploadStoreImage(HttpServletRequest request, @RequestPart MultipartFile storeImage , @PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        try {
            int isUpdated = storeService.updateStoreImage(storeImage, slug);
            if (isUpdated > 0){
                responseObj.put("message","successfully updated");
                responseObj.put("status",200);
            }else{
                responseObj.put("message","No record found.");
                responseObj.put("status",404);
            }

        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();;
            responseObj.put("message",e.getMessage());
            responseObj.put("status",500);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));


    }



    @Transactional
    @PostMapping("/status")
    public ResponseEntity<Map<String,Object>> updateStoreStatus (@RequestBody StatusDto statusDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        int isUpdated = storeService.updateStatusBySlug(statusDto);
        if (isUpdated > 0) {
            responseObj.put("message", "Store's status has been successfully updated.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "No store found.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @Value("${store.get}")
    String filePath;

    @GetMapping("/image/{slug}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable(required = true) String filename , @PathVariable String slug) throws Exception {
        Path path = Paths.get(filePath +slug+"/"+ filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }


    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    @PostMapping("category")
    public ResponseEntity<List<StoreCategory>> getAllStoreCategory(@RequestBody SearchFilters searchFilters) {
        List<StoreCategory> storeCategories = storeService.getAllStoreCategory(searchFilters);
        return new ResponseEntity<>(storeCategories, HttpStatus.OK);
    }

    @PostMapping(value = {"category/add","category/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemCategory(@RequestBody CategoryDto categoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> result = new HashMap<>();
        StoreCategory updatedStoreCategory = storeService.saveOrUpdateStoreCategory(categoryDto);
        if(updatedStoreCategory != null) {
             result.put("res",updatedStoreCategory);
            if(categoryDto.getId() !=null && categoryDto.getId() != 0) {
                result.put("message", "Category successfully updated.");
                result.put("status", 200);
            }else {
                result.put("message", "Category successfully inserted.");
                result.put("status", 201);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }


    @GetMapping("category/{categoryId}")
    public ResponseEntity<StoreCategory> getAllCategory(@PathVariable Integer categoryId) {
        StoreCategory storeCategory = storeService.getStoreCategoryById(categoryId);
        return new ResponseEntity<>(storeCategory, HttpStatus.OK);
    }


    @PostMapping("category/delete")
    public ResponseEntity<Map<String,Object>> deleteItemCategoryById(HttpServletRequest request ,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = storeService.deleteStoreCategory(deleteDto,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Store's category was successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No category found to delete.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    @PostMapping("subcategory")
    public ResponseEntity<List<StoreSubCategory>> getStoreSubCategory(@RequestBody SearchFilters searchFilters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<StoreSubCategory> storeSubCategories = storeService.getAllStoreSubCategories(searchFilters);
        return new ResponseEntity<>(storeSubCategories, HttpStatus.OK);
    }


    @PostMapping("subcategory/delete")
    public ResponseEntity<Map<String,Object>> deleteItemSubCategoryById(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = storeService.deleteStoreSubCategory(deleteDto,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Store's subcategory successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "No subcategory found to delete.");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping(value = {"subcategory/add","subcategory/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemSubCategory(@RequestBody SubCategoryDto subCategoryDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> result = new HashMap<>();
        StoreSubCategory updatedStoreSubCategory = storeService.saveOrUpdateStoreSubCategory(subCategoryDto);
        if(updatedStoreSubCategory != null) {
            result.put("res",updatedStoreSubCategory);
            if(subCategoryDto.getId() != null) {
                result.put("message", "Subcategory successfully updated.");
                result.put("status", 200);
            }else {
                result.put("message", "Subcategory successfully inserted.");
                result.put("status", 201);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }



}
