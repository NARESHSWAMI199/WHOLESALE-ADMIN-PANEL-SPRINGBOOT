package com.sales.admin.controllers;


import com.sales.admin.services.ServicePlanService;
import com.sales.admin.services.UserService;
import com.sales.dto.DeleteDto;
import com.sales.dto.ServicePlanDto;
import com.sales.dto.StatusDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.WholesalerPlans;
import com.sales.global.ConstantResponseKeys;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ServicePlanController  {

    private final UserService userService;
    private final ServicePlanService servicePlanService;
    
    private static final Logger logger = LoggerFactory.getLogger(ServicePlanController.class);

    @PostMapping(value = {"user-plans/{userSlug}","user-plans"})
    public ResponseEntity< Page<WholesalerPlans>> getUserPlans(@PathVariable(required = false) String userSlug, @RequestBody UserPlanDto searchFilters){
        logger.debug("Fetching user plans for userSlug: {}", userSlug);
        Integer userId = userService.getUserIdBySlug(userSlug);
        Page<WholesalerPlans> allUserPlans = servicePlanService.getAllUserPlans(userId, searchFilters);
        return new ResponseEntity<>(allUserPlans,HttpStatus.OK);
    }


    @PostMapping("service-plans")
    public ResponseEntity<Page<ServicePlan>> getAllPlans(@RequestBody ServicePlanDto servicePlanDto) {
        logger.debug("Fetching all service plans with filters: {}", servicePlanDto);
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
        logger.debug("Inserting new service plan: {}", servicePlanDto);
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> result = new HashMap<>();
        ServicePlan servicePlan = servicePlanService.insertServicePlan(loggedUser,servicePlanDto);
        result.put(ConstantResponseKeys.RES,servicePlan);
        result.put(ConstantResponseKeys.MESSAGE,"Service plan added successfully.");
        result.put(ConstantResponseKeys.STATUS , 201);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }


    @PostMapping(ConstantResponseKeys.STATUS)
    public ResponseEntity<Map<String,Object>> updateStatus(HttpServletRequest request, @RequestBody StatusDto statusDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Updating status for service plan: {}", statusDto);
        User loggedUser = (User) request.getAttribute("user");
        Map<String, Object> result = servicePlanService.updateServicePlanStatus(statusDto, loggedUser);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }


    @PostMapping("delete")
    public ResponseEntity<Map<String,Object>> deleteStatus(@RequestBody DeleteDto deleteDto, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.debug("Deleting service plan: {}", deleteDto);
        User loggedUser = (User) request.getAttribute("user");
        Map<String, Object> result = servicePlanService.deletedServicePlan(deleteDto,loggedUser);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }

}
