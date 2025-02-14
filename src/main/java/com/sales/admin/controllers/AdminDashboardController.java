package com.sales.admin.controllers;

import com.sales.dto.GraphDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("admin/dashboard")
public class AdminDashboardController extends ServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getAllDashboardCount() {
        logger.info("Fetching all dashboard counts");
        Map<String, Object> responseObj = new HashMap<>();
        responseObj.put("users", userService.getUserCounts());
        responseObj.put("retailers", userService.getRetailersCounts());
        responseObj.put("wholesalers", userService.getWholesalersCounts());
        responseObj.put("staffs", userService.getStaffsCounts());
        responseObj.put("admins", userService.getAdminsCounts());
        // responseObj.put("items", itemService.getItemCounts());
        // responseObj.put("wholesales", storeService.getWholesaleCounts());
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @PostMapping("graph/months/")
    public ResponseEntity<Map<String, Object>> getAllGraphData(@RequestBody GraphDto graphDto) {
        logger.info("Fetching graph data for months with filters: {}", graphDto);
        Map<String, Object> responseObj = new HashMap<>();
        responseObj.put("res", storeService.getStoreCountByMonths(graphDto));
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }
}
