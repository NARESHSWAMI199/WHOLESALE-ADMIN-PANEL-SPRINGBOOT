package com.sales.admin.controllers;


import com.sales.dto.GraphDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("admin/dashboard")
public class AdminDashboardController extends ServiceContainer {


    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getAllDashboardCount() {
        Map responseObj = new HashMap();
        responseObj.put("users" , userService.getUserCounts());
        responseObj.put("retailers" , userService.getRetailersCounts());
        responseObj.put("wholesalers" , userService.getWholesalersCounts());
        responseObj.put("staffs" , userService.getStaffsCounts());
        responseObj.put("items",itemService.getItemCounts());
        responseObj.put("wholesales",storeService.getWholesaleCounts());
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