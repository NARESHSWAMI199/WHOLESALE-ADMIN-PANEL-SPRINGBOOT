package com.sales.admin.controllers;


import com.sales.entities.ServicePlan;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("plans")
public class ServicePlanController extends ServiceContainer {

    @GetMapping("/all")
    public ResponseEntity<List<ServicePlan>> getAllPlans() {
        return new ResponseEntity<>(servicePlanService.getALlServicePlan(), HttpStatusCode.valueOf(200));
    }


}
