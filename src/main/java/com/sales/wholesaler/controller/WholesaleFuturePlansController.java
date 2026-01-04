package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.entities.SalesUser;
import com.sales.entities.WholesalerFuturePlan;
import com.sales.global.ConstantResponseKeys;
import com.sales.jwtUtils.JwtToken;
import com.sales.utils.Utils;
import com.sales.wholesaler.services.WholesaleFuturePlansService;
import com.sales.wholesaler.services.WholesaleUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("future/plans")
@RequiredArgsConstructor
public class WholesaleFuturePlansController  {


    private final  JwtToken jwtToken;
    private final WholesaleUserService wholesaleUserService;
    private final WholesaleFuturePlansService wholesaleFuturePlansService;


    @PostMapping("/")
    public ResponseEntity<Page<WholesalerFuturePlan>> getAllWholesalerFuturePlans(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        SalesUser loggedUser = Utils.getUserFromRequest(request,jwtToken, wholesaleUserService);
        Page<WholesalerFuturePlan> wholesalerFuturePlans = wholesaleFuturePlansService.getWholesalerFuturePlans(loggedUser,searchFilters);
        return new ResponseEntity<>(wholesalerFuturePlans, HttpStatusCode.valueOf(200));
    }


    @PostMapping("/activate")
    public ResponseEntity<Map<String,Object>> activateFuturePlan(HttpServletRequest request, @RequestBody Map<String,String> data){
        SalesUser loggedUser = Utils.getUserFromRequest(request,jwtToken, wholesaleUserService);
        String slug = data.get("slug");
        Map<String,Object> result = new HashMap<>();
        int activated = wholesaleFuturePlansService.activateWholesalerFuturePlans(loggedUser, slug);
        if(activated > 0){
            result.put(ConstantResponseKeys.MESSAGE,"Future plan activated successfully.");
            result.put(ConstantResponseKeys.STATUS,200);
        }else {
            result.put(ConstantResponseKeys.MESSAGE,"No plan found to activate.");
            result.put(ConstantResponseKeys.STATUS,400); // it's bad request.
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }



}
