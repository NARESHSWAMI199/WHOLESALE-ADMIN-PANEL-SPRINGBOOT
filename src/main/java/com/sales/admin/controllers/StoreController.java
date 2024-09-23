package com.sales.admin.controllers;

import com.sales.dto.SearchFilters;
import com.sales.dto.StatusDto;
import com.sales.dto.StoreDto;
import com.sales.entities.Store;
import com.sales.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("admin/store")
public class StoreController extends ServiceContainer{

    @PostMapping("/all")
    public ResponseEntity<Page<Store>> getAllStore(@RequestBody SearchFilters searchFilters){
        Page<Store> storePage =  storeService.getAllStore(searchFilters);
        return new ResponseEntity<>(storePage, HttpStatus.OK);
    }

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
    public ResponseEntity<Map<String,Object>> addStoreOrUpdateStore(HttpServletRequest request, @RequestBody StoreDto storeDto) {
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

}
