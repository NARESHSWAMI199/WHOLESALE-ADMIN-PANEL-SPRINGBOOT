package com.sales.wholesaler.controller;


import com.sales.admin.controllers.ServiceContainer;
import com.sales.entities.ServicePlan;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("wholesale/plans")
public class WholesaleServicePlanController extends WholesaleServiceContainer {

    @GetMapping("/all")
    public ResponseEntity<List<ServicePlan>> getAllPlans() {
        return new ResponseEntity<>(wholesaleServicePlanService.getALlServicePlan(), HttpStatusCode.valueOf(200));
    }


}
