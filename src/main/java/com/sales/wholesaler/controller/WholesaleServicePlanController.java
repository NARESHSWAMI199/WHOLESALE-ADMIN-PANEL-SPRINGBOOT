package com.sales.wholesaler.controller;


import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wholesale/plan")
public class WholesaleServicePlanController extends WholesaleServiceContainer {


    @GetMapping("/all")
    public ResponseEntity<List<ServicePlan>> getAllPlans() {
        return new ResponseEntity<>(wholesaleServicePlanService.getALlServicePlan(), HttpStatusCode.valueOf(200));
    }

    @PostMapping("/my-plans")
    public ResponseEntity<Page<UserPlans>> getAllPlans(HttpServletRequest request, @RequestBody UserPlanDto searchFilters) {
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Page<UserPlans> allUserPlans = wholesaleServicePlanService.getAllUserPlans(loggedUser, searchFilters);
        return new ResponseEntity<>(allUserPlans, HttpStatusCode.valueOf(200));
    }

    @GetMapping("is-active")
    public ResponseEntity<Map<String,Object>> isUserPlanActive(HttpServletRequest request){
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        Map<String,Object> result = new HashMap<>();
        boolean planIsActive = wholesaleServicePlanService.isPlanActive(loggedUser.getActivePlan());
        result.put("planIsActive",planIsActive);
        result.put("status" , 200);
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

}