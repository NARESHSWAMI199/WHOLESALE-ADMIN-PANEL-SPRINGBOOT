package com.sales.wholesaler.controller;


import com.sales.dto.GraphDto;
import com.sales.entities.AuthUser;
import com.sales.global.ConstantResponseKeys;
import com.sales.wholesaler.services.WholesaleItemService;
import com.sales.wholesaler.services.WholesaleStoreService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesale/dashboard")
@RequiredArgsConstructor
public class WholesaleDashboardController  {


    private final WholesaleStoreService wholesaleStoreService;
    private final WholesaleItemService wholesaleItemService;
    private static final Logger logger = LoggerFactory.getLogger(WholesaleDashboardController.class);

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Object>> getAllDashboardCount(Authentication authentication,HttpServletRequest request) {
        logger.debug("Starting getAllDashboardCount method");
        AuthUser loggedUser = (AuthUser) authentication.getPrincipal();
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        Map<String,Object> responseObj = new HashMap<>();
        responseObj.put("items" , wholesaleItemService.getItemCounts(storeId));
        responseObj.put("newItems" , wholesaleItemService.getItemCountsForNewLabel(storeId));
        responseObj.put("oldItems" , wholesaleItemService.getItemCountsForOldLabel(storeId) );
        responseObj.put("inStock" , wholesaleItemService.getItemCountsForInStock(storeId));
        responseObj.put("outStock" , wholesaleItemService.getItemCountsForOutStock(storeId));
        responseObj.put(ConstantResponseKeys.STATUS, 200);
        logger.debug("Completed getAllDashboardCount method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get(ConstantResponseKeys.STATUS)));
    }

    @PostMapping("graph/months/")
    public ResponseEntity<Map<String, Object>> getAllGraphData(Authentication authentication, HttpServletRequest request, @RequestBody GraphDto graphDto) {
        logger.debug("Starting getAllGraphData method");
        Map<String,Object> responseObj = new HashMap<>();
        AuthUser loggedUser = (AuthUser) authentication.getPrincipal();
        Integer storeId = wholesaleStoreService.getStoreIdByUserSlug(loggedUser.getId());
        responseObj.put(ConstantResponseKeys.RES ,wholesaleItemService.getItemCountByMonths(graphDto,storeId));
        responseObj.put(ConstantResponseKeys.STATUS, 200);
        logger.debug("Completed getAllGraphData method");
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get(ConstantResponseKeys.STATUS)));
    }
}
