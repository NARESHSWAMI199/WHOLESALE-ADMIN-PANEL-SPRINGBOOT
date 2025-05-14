package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.entities.User;
import com.sales.entities.WholesalerFuturePlan;
import com.sales.jwtUtils.JwtToken;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WholesaleFuturePlansController extends WholesaleServiceContainer {


    @Autowired
    JwtToken jwtToken;


    @PostMapping("/")
    public ResponseEntity<Page<WholesalerFuturePlan>> getAllWholesalerFuturePlans(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        User loggedUser = Utils.getUserFromRequest(request,jwtToken, wholesaleUserService);
        Page<WholesalerFuturePlan> wholesalerFuturePlans = wholesaleFuturePlansService.getWholesalerFuturePlans(loggedUser,searchFilters);
        return new ResponseEntity<>(wholesalerFuturePlans, HttpStatusCode.valueOf(200));
    }


    @PostMapping("/activate")
    public ResponseEntity<Map<String,Object>> activateFuturePlan(HttpServletRequest request, @RequestBody Map<String,String> data){
        User loggedUser = Utils.getUserFromRequest(request,jwtToken, wholesaleUserService);
        String slug = data.get("slug");
        Map<String,Object> result = new HashMap<>();
        int activated = wholesaleFuturePlansService.activateWholesalerFuturePlans(loggedUser, slug);
        if(activated > 0){
            result.put("message","Future plan activated successfully.");
            result.put("status",200);
        }else {
            result.put("message","No plan found to activate.");
            result.put("status",400); // it's bad request.
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }



}
