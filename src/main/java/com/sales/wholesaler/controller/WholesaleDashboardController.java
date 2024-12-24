package com.sales.wholesaler.controller;


import com.sales.dto.GraphDto;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesale/dashboard")
public class WholesaleDashboardController extends WholesaleServiceContainer {


    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getAllDashboardCount(HttpServletRequest request) {
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        Map responseObj = new HashMap();
        responseObj.put("items" , wholesaleItemService.getItemCounts(storeId));
        responseObj.put("newItems" , wholesaleItemService.getItemCountsForNewLabel(storeId));
        responseObj.put("oldItems" , wholesaleItemService.getItemCountsForOldLabel(storeId) );
        responseObj.put("inStock" , wholesaleItemService.getItemCountsForInStock(storeId));
        responseObj.put("outStock" , wholesaleItemService.getItemCountsForOutStock(storeId));
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @PostMapping("graph/months/")
    public ResponseEntity<Map<String, Object>> getAllGraphData(HttpServletRequest request,@RequestBody GraphDto graphDto) {
        Map responseObj = new HashMap();
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        responseObj.put("res" ,wholesaleItemService.getItemCountByMonths(graphDto,storeId));
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }
}
