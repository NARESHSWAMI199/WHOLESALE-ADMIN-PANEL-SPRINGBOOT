package com.sales.admin.controllers;


import com.sales.dto.ServicePlanDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
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
@RequestMapping("admin/plans/")
public class ServicePlanController extends ServiceContainer {




    @PostMapping("user-plans/{slug}")
    public ResponseEntity< Page<UserPlans>> getUserPlans(@PathVariable String slug, @RequestBody UserPlanDto searchFilters){
        Integer userId = userService.getUserIdBySlug(slug);
        Page<UserPlans> allUserPlans = servicePlanService.getAllUserPlans(userId, searchFilters);
        return new ResponseEntity<>(allUserPlans,HttpStatus.OK);
    }


    @GetMapping("service-plans")
    public ResponseEntity<List<ServicePlan>> getAllPlans() {
        return new ResponseEntity<>(servicePlanService.getALlServicePlan(), HttpStatusCode.valueOf(200));
    }

    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> insertServicePlans(HttpServletRequest request , @RequestBody ServicePlanDto servicePlanDto){
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> result = new HashMap<>();
        ServicePlan servicePlan = servicePlanService.insertServicePlan(loggedUser,servicePlanDto);
        if(servicePlan.getId() > 0){
            result.put("message","Service plan added successfully.");
            result.put("status" , 200);
        }else {
            result.put("message", "Something went wrong.");
            result.put("status", 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


}
