package com.sales.payment.controller;


import com.cashfree.ApiException;
import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;
import com.cashfree.model.OrderMeta;
import com.google.gson.Gson;
import com.sales.dto.CashfreeDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
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
import java.util.UUID;

@Controller
@RequestMapping("cashfree")
public class CashFreePgController extends PaymentServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(CashFreePgController.class);

    @Value("${cashfree.test.key}")
    String testSaltKey;

    @Value("${cashfree.test.mid}")
    public String testMid;

    @Value("${cashfree.mobile}")
    String mobileNumber;

    @ResponseBody
    @PostMapping("sessionId")
    public ResponseEntity<Map<String,Object>> getPaymentSessionId (@RequestBody CashfreeDto cashfreeDto) {
        User loggedUser = wholesaleUserService.findUserBySlug(cashfreeDto.getUserSlug());
        logger.info("Received request to get payment session ID for service slug : {} and username : {} and user slug : {}",cashfreeDto.getServicePlanSlug(),loggedUser.getUsername(),loggedUser.getSlug());
        String slug = UUID.randomUUID().toString();
        ServicePlan servicePlan = servicePlanService.findBySlug(cashfreeDto.getServicePlanSlug());
        long amount = (servicePlan.getPrice()-servicePlan.getDiscount());
        Map<String,Object> result = new HashMap<>();
        try {
            logger.info("amount {}",amount);
            String mid = testMid;
            String key = testSaltKey;
            Cashfree.XClientId = mid;
            Cashfree.XClientSecret = key;
            Cashfree.XEnvironment = Cashfree.SANDBOX;

            CustomerDetails customerDetails = new CustomerDetails();
            customerDetails.setCustomerId(UUID.randomUUID().toString());
            customerDetails.setCustomerPhone(mobileNumber);

            CreateOrderRequest request = new CreateOrderRequest();
            OrderMeta orderMeta = new OrderMeta();
            orderMeta.setReturnUrl("http://localhost:8080/cashfree/home");
            orderMeta.setNotifyUrl("http://localhost:8080/cashfree/callback/"+slug+"/"+loggedUser.getId()+"/"+servicePlan.getId());
            request.setOrderMeta(orderMeta);
            request.setOrderAmount((double) amount);
            request.setOrderCurrency("INR");
            request.setCustomerDetails(customerDetails);
            Cashfree cashfree = new Cashfree();
            ApiResponse<OrderEntity> response = cashfree.PGCreateOrder("2023-08-01", request, null, null, null);
            logger.info("Payment session ID generated successfully: {}", response.getData().getPaymentSessionId());
            logger.info("The order id for payment : {}",response.getData().getOrderId());

            cashfreeDto.setSlug(slug);
            cashfreeDto.setAmount((double) amount);
            cashfreeDto.setCurrency("INR");
            cashfreeDto.setOrderId(response.getData().getOrderId());
            cashfreeDto.setStatus("VISITED");
            cashfreeService.insertPaymentDetail(cashfreeDto);
            result.put("res",response.getData());
            result.put("status" , 200);
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

    @GetMapping("pay/{servicePlanSlug}/{token}")
    public String redirectPaymentPage(HttpServletRequest request,@PathVariable String servicePlanSlug, @PathVariable String token, Model model) {
        User loggedUser = Utils.getUserFromRequest(request,token,jwtToken,wholesaleUserService);
        logger.info("Redirecting to payment page for servicePlanSlug : {} user : {} and user slug : {}", servicePlanSlug,loggedUser.getUsername(),loggedUser.getSlug());
        model.addAttribute("servicePlanSlug",servicePlanSlug);
        model.addAttribute("userSlug",loggedUser.getSlug());
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

            String orderId = (String) order.get("order_id");
            String cfPaymentId = (String) payment.get("cf_payment_id");
            String paymentStatus = payment.getString("payment_status");
            String paymentAmount = String.valueOf(payment.get("payment_amount"));
            String paymentCurrency = payment.getString("payment_currency");
            String paymentMessage = payment.getString("payment_message");
            String paymentTime = (String) payment.get("payment_time");
            String bankReference = (String) payment.get("bank_reference");
            String paymentGroup = payment.getString("payment_group");
            String paymentMethod = payment.getJSONObject("payment_method").toString();

            CashfreeDto cashfreeDto = CashfreeDto.builder()
                    .orderId(orderId)
                    .slug(slug)
                    .cfPaymentId(cfPaymentId)
                    .status(paymentStatus)
                    .amount(Double.parseDouble(paymentAmount))
                    .currency(paymentCurrency)
                    .message(paymentMessage)
                    .paymentTime(paymentTime)
                    .bankReference(bankReference)
                    .paymentType(paymentGroup)
                    .paymentMethod(paymentMethod)
                    .actualResponse(paymentResponseStr)
                    .build();

            int isUpdated = cashfreeService.updatePaymentCallback(cashfreeDto,userId);
            wholesaleServicePlanService.assignUserPlan(userId, servicePlanId);
            logger.info("PhonePe callback processed successfully for user: {}", userId);
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
