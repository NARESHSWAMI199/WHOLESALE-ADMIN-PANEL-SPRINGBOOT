package com.sales.admin.controllers;


import com.sales.dto.DeleteDto;
import com.sales.dto.ServicePlanDto;
import com.sales.dto.StatusDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.WholesalerPlans;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("admin/plans/")
public class ServicePlanController extends ServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(ServicePlanController.class);

    @PostMapping(value = {"user-plans/{userSlug}","user-plans"})
    public ResponseEntity< Page<WholesalerPlans>> getUserPlans(@PathVariable(required = false) String userSlug, @RequestBody UserPlanDto searchFilters){
        logger.info("Fetching user plans for userSlug: {}", userSlug);
        Integer userId = userService.getUserIdBySlug(userSlug);
        Page<WholesalerPlans> allUserPlans = servicePlanService.getAllUserPlans(userId, searchFilters);
        return new ResponseEntity<>(allUserPlans,HttpStatus.OK);
    }


    @PostMapping("service-plans")
    public ResponseEntity<Page<ServicePlan>> getAllPlans(@RequestBody ServicePlanDto servicePlanDto) {
        logger.info("Fetching all service plans with filters: {}", servicePlanDto);
        return new ResponseEntity<>(servicePlanService.getALlServicePlan(servicePlanDto), HttpStatusCode.valueOf(200));
    }


    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(
            example = """
               {
                     "planName": "string",
                      "months": 0,
                      "price": 0,
                      "discount": 0,
                      "description": "string"
                }
            """))
    )
    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> insertServicePlans(HttpServletRequest request , @RequestBody ServicePlanDto servicePlanDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Inserting new service plan: {}", servicePlanDto);
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> result = new HashMap<>();
        ServicePlan servicePlan = servicePlanService.insertServicePlan(loggedUser,servicePlanDto);
        result.put("res",servicePlan);
        result.put("message","Service plan added successfully.");
        result.put("status" , 201);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("status")
    public ResponseEntity<Map<String,Object>> updateStatus(HttpServletRequest request, @RequestBody StatusDto statusDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Updating status for service plan: {}", statusDto);
        User loggedUser = (User) request.getAttribute("user");
        Map<String, Object> result = servicePlanService.updateServicePlanStatus(statusDto, loggedUser);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("delete")
    public ResponseEntity<Map<String,Object>> deleteStatus(@RequestBody DeleteDto deleteDto, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Deleting service plan: {}", deleteDto);
        User loggedUser = (User) request.getAttribute("user");
        Map<String, Object> result = servicePlanService.deletedServicePlan(deleteDto,loggedUser);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}
