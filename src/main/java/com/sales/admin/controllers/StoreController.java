package com.sales.admin.controllers;

import com.sales.dto.*;
import com.sales.entities.Store;
import com.sales.entities.StoreCategory;
import com.sales.entities.StoreSubCategory;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
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
    @GetMapping("/delete/{slug}")
    public ResponseEntity<Map<String,Object>> deleteStore(@PathVariable String slug) {
        Map responseObj = new HashMap();
        int isUpdated = storeService.deleteStoreBySlug(slug);
        if (isUpdated > 0) {
            responseObj.put("message", "Store has been successfully deleted.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String,Object>> getStoreDetailBySlug(@PathVariable String slug) {
        Map responseObj = new HashMap();
        Store store = storeService.getStoreDetails(slug);
        if (store!= null){
            responseObj.put("res", store);
            responseObj.put("status", 200);
        }else {
            responseObj.put("message", "Please check you parameters not a valid request.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @GetMapping("/detailbyuser/{userSLug}")
    public ResponseEntity<Map<String,Object>> getUserDetailByUserSlug(@PathVariable("userSLug") String slug) throws Exception {
        Map responseObj = new HashMap();
        Store store = storeService.getStoreByUserSlug(slug);
        if (store!= null){
            responseObj.put("res", store);
            responseObj.put("status", 200);
        }else {
            responseObj.put("message", "Please check you parameters not a valid request.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));
    }




    @Transactional
    @PostMapping(value = {"/add","/update"})
    public ResponseEntity<Map<String,Object>> addStoreOrUpdateStore(HttpServletRequest request,  @ModelAttribute StoreDto storeDto) {
        Map responseObj = new HashMap();
        try{
            User logggedUser = (User) request.getAttribute("user");
            responseObj = storeService.createOrUpdateStore(storeDto,logggedUser);
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();;
            responseObj.put("message",e.getMessage());
            responseObj.put("status",500);
        }
        return new ResponseEntity<>(responseObj,HttpStatus.valueOf((Integer) responseObj.get("status")));


    }


    /** currently we are not using for upload store image */
    @Transactional
    @PostMapping("profile/{slug}")
    public ResponseEntity<Map<String,Object>> uploadStoreImage(HttpServletRequest request, @RequestPart MultipartFile storeImage , @PathVariable String slug) {
        Map responseObj = new HashMap();
        try {
            User logggedUser = (User) request.getAttribute("user");
            int isUpdated = storeService.updateStoreImage(storeImage, slug);
            if (isUpdated > 0){
                responseObj.put("message","successfully updated");
                responseObj.put("status",201);
            }else{
                responseObj.put("message","Something went wrong. Please recheck you parameters.");
                responseObj.put("status",400);
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
    public ResponseEntity<Map<String,Object>> stockSlug (@RequestBody StatusDto statusDto) {
        Map responseObj = new HashMap();
        int isUpdated = storeService.updateStatusBySlug(statusDto);
        if (isUpdated > 0) {
            responseObj.put("message", "Store's status has been successfully updated.");
            responseObj.put("status", 200);
        }else{
            responseObj.put("message", "There is nothing to update recheck you parameters");
            responseObj.put("status", 400);
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


    @GetMapping("category/{categoryId}")
    public ResponseEntity<StoreCategory> getAllCategory(@PathVariable Integer categoryId) {
        StoreCategory storeCategory = storeService.getStoreCategoryById(categoryId);
        return new ResponseEntity<>(storeCategory, HttpStatus.OK);
    }



    @Transactional(rollbackOn = {MyException.class ,RuntimeException.class})
    @PostMapping("subcategory")
    public ResponseEntity<List<StoreSubCategory>> getStoreSubCategory(@RequestBody SearchFilters searchFilters) {
        List<StoreSubCategory> storeSubCategories = storeService.getAllStoreSubCategories(searchFilters);
        return new ResponseEntity<>(storeSubCategories, HttpStatus.OK);
    }



    @PostMapping(value = {"category/add","category/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemCategory(@RequestBody CategoryDto categoryDto) {
        Map<String,Object> result = new HashMap<>();
        StoreCategory updatedStoreCategory = storeService.saveOrUpdateStoreCategory(categoryDto);
        if(updatedStoreCategory != null) {
            if(categoryDto.getId() != null) {
                result.put("message", "Category successfully updated.");
                result.put("status", 200);
            }else {
                result.put("message", "Category successfully inserted.");
                result.put("status", 201);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }


    @GetMapping("category/delete/{categorySlug}")
    public ResponseEntity<Map<String,Object>> deleteItemCategoryById(HttpServletRequest request ,@PathVariable String categorySlug) {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = storeService.deleteStoreCategory(categorySlug,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Store's category was successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }

    @GetMapping("subcategory/delete/{subcategoryId}")
    public ResponseEntity<Map<String,Object>> deleteItemSubCategoryById(HttpServletRequest request ,@PathVariable String subcategoryId) {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        int isUpdated = storeService.deleteStoreSubCategory(subcategoryId,user);
        if (isUpdated > 0) {
            responseObj.put("message", "Store's subcategory was successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }






    @PostMapping(value = {"subcategory/add","subcategory/update"})
    public ResponseEntity<Map<String,Object>> saveOrUpdateItemSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        Map<String,Object> result = new HashMap<>();
        StoreSubCategory updatedStoreSubCategory = storeService.saveOrUpdateStoreSubCategory(subCategoryDto);
        if(updatedStoreSubCategory != null) {
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



}
