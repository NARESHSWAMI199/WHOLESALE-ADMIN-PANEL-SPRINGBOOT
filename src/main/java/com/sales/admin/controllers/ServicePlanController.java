package com.sales.admin.controllers;


import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin/plans/")
public class ServicePlanController extends ServiceContainer {




    @PostMapping("user-plans/{slug}")
    public ResponseEntity<List<Map<String,Object>>> getUserPlans(@PathVariable String slug, @RequestBody UserPlanDto searchFilters){
        Integer userId = userService.getUserIdBySlug(slug);
        List<Map<String, Object>> allUserPlans = servicePlanService.getAllUserPlans(userId,searchFilters);
        return new ResponseEntity<>(allUserPlans, HttpStatus.OK);
    }


    @GetMapping("service-plans")
    public ResponseEntity<List<ServicePlan>> getAllPlans() {
        return new ResponseEntity<>(servicePlanService.getALlServicePlan(), HttpStatusCode.valueOf(200));
    }


}
