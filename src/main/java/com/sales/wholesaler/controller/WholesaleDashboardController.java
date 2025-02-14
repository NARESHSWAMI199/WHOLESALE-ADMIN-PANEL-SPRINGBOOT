package com.sales.wholesaler.controller;


import com.sales.dto.GraphDto;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesale/dashboard")
public class WholesaleDashboardController extends WholesaleServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleDashboardController.class);

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getAllDashboardCount(HttpServletRequest request) {
        logger.info("Starting getAllDashboardCount method");
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        Map<String,Object> responseObj = new HashMap<>();
        responseObj.put("items" , wholesaleItemService.getItemCounts(storeId));
        responseObj.put("newItems" , wholesaleItemService.getItemCountsForNewLabel(storeId));
        responseObj.put("oldItems" , wholesaleItemService.getItemCountsForOldLabel(storeId) );
        responseObj.put("inStock" , wholesaleItemService.getItemCountsForInStock(storeId));
        responseObj.put("outStock" , wholesaleItemService.getItemCountsForOutStock(storeId));
        responseObj.put("status", 200);
        logger.info("Completed getAllDashboardCount method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @PostMapping("graph/months/")
    public ResponseEntity<Map<String, Object>> getAllGraphData(HttpServletRequest request,@RequestBody GraphDto graphDto) {
        logger.info("Starting getAllGraphData method");
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        responseObj.put("res" ,wholesaleItemService.getItemCountByMonths(graphDto,storeId));
        responseObj.put("status", 200);
        logger.info("Completed getAllGraphData method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }
}
