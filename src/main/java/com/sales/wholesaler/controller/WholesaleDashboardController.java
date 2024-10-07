package com.sales.wholesaler.controller;


import com.sales.dto.GraphDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesaler/dashboard")
public class WholesaleDashboardController extends WholesaleServiceContainer {


    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getAllDashboardCount() {
        Map responseObj = new HashMap();
        responseObj.put("items" , wholesaleItemService.getItemCounts());
        responseObj.put("newItems" , wholesaleItemService.getItemCountsForNewLabel());
        responseObj.put("oldItems" , wholesaleItemService.getItemCountsForOldLabel() );
        responseObj.put("inStock" , wholesaleItemService.getItemCountsForInStock());
        responseObj.put("outStock" , wholesaleItemService.getItemCountsForOutStock());
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @PostMapping("graph/months/")
    public ResponseEntity<Map<String, Object>> getAllGraphData(@RequestBody GraphDto graphDto) {
        Map responseObj = new HashMap();
        responseObj.put("res" ,storeService.getStoreCountByMonths(graphDto));
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }
}
