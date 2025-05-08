package com.sales.payment.controller;


import com.cashfree.ApiException;
import com.cashfree.model.OrderEntity;
import com.google.gson.Gson;
import com.sales.dto.CashfreeDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.exceptions.NotFoundException;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("cashfree")
public class CashFreePgController extends PaymentServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(CashFreePgController.class);

    @Value("${cashfree.env}")
    String env;

    @ResponseBody
    @PostMapping(value = {"sessionId"})
    public ResponseEntity<Map<String,Object>> getPaymentSessionId (HttpServletRequest request,@RequestParam(value = "redirectUri", required = false) String redirectUri, @RequestBody CashfreeDto cashfreeDto) {
        User loggedUser = wholesaleUserService.findUserBySlug(cashfreeDto.getUserSlug());
        if(loggedUser == null) throw new NotFoundException("No logged user found.");

        logger.info("Received request to get payment session ID for service slug : {} and username : {} and user slug : {}",cashfreeDto.getServicePlanSlug(),loggedUser.getUsername(),loggedUser.getSlug());
        ServicePlan servicePlan = servicePlanService.findBySlug(cashfreeDto.getServicePlanSlug());
        if(servicePlan == null) throw new NotFoundException("No service plan found.");
        Map<String,Object> result = new HashMap<>();
        try {
            OrderEntity orderEntity = cashfreeService.getOrderEntityForCashfreePayment(request,cashfreeDto, loggedUser, servicePlan,redirectUri,env);
            result.put("res",orderEntity);
            result.put("status" , 201);
        }
        catch (ApiException e){
            logger.error("Exception occurred while getting payment session ID : {}", e.getMessage());
            result.put("message", "Something went wrong during getPaymentSessionId payment. please contact to administrator.");
            result.put("status",500);
            logger.info( "Exception occur in  getPaymentSessionId :: {}", e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @GetMapping(value = {"pay/{servicePlanSlug}/{token}"})
    public String redirectPaymentPage(HttpServletRequest request,@PathVariable String servicePlanSlug, @PathVariable String token, @RequestParam(value = "redirectUri", required = false) String redirectUri, Model model) {
        User loggedUser = Utils.getUserFromRequest(request,token,jwtToken,wholesaleUserService);
        logger.info("Redirecting to payment page for servicePlanSlug : {} user : {} and user slug : {} and redirect uri : {}", servicePlanSlug,loggedUser.getUsername(),loggedUser.getSlug(),redirectUri);
        model.addAttribute("servicePlanSlug",servicePlanSlug);
        model.addAttribute("userSlug",loggedUser.getSlug());
        model.addAttribute("env",env);
        model.addAttribute("redirectUri",redirectUri);
        return "cashfree";
    }



    @RequestMapping("callback/{slug}/{userId}/{servicePlanId}")
    public ResponseEntity<Map<String,Object>> saveCashfreeCallback(@PathVariable String slug, @PathVariable Integer userId, @PathVariable  Integer servicePlanId, @RequestBody Map<String,Object> data) {
        Map<String,Object> result = new HashMap<>();
        try {
            logger.info("Response getting from callback  : {} ", data.toString());
            String paymentResponseStr = new Gson().toJson(data.get("data"));
            JSONObject paymentDetails = new JSONObject(paymentResponseStr);
            JSONObject order = null;
            JSONObject payment = null;

            if (paymentDetails.has("order")) order = paymentDetails.getJSONObject("order");
            if (paymentDetails.has("payment")) payment = paymentDetails.getJSONObject("payment");

            assert order != null;
            assert payment != null;

            int isUpdated = cashfreeService.updateCashfreeCallback(order, payment, slug, userId,servicePlanId,paymentResponseStr);
            logger.info("Cashfree callback processed successfully for user: {} and status isUpdated : {}",userId,isUpdated);
            result.put("isUpdate", isUpdated > 0);
            result.put("response", data);
            result.put("status", 200);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Exception during cashfree callback");
            result.put("message", "Something went wrong during cashfree callback. please contact to administrator.");
            result.put("status",500);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }



}
