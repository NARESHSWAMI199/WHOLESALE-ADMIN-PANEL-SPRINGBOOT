package com.sales.wholesaler.controller;


import com.cashfree.ApiException;
import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;
import com.sales.dto.CashfreeDto;
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
public class CashFreePgController extends WholesaleServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(CashFreePgController.class);

    @Value("${cashfree.test.key}")
    String testSaltKey;

    @Value("${cashfree.test.mid}")
    public String testMid;
    @ResponseBody
    @PostMapping("sessionId")
    public ResponseEntity<Map<String,Object>> getPaymentSessionId (@RequestBody CashfreeDto cashfreeDto) {
        logger.info("Received request to get payment session ID for mobile number: {}", cashfreeDto.getMobileNumber());
        Map<String,Object> result = new HashMap<>();
        try {
            System.err.println(cashfreeDto.getMobileNumber() + " : "+ cashfreeDto.getAmount());
            String mid = testMid;
            String key = testSaltKey;
            Cashfree.XClientId = mid;
            Cashfree.XClientSecret = key;
            Cashfree.XEnvironment = Cashfree.SANDBOX;

            CustomerDetails customerDetails = new CustomerDetails();
            customerDetails.setCustomerId(UUID.randomUUID().toString());
            customerDetails.setCustomerPhone(cashfreeDto.getMobileNumber());

            CreateOrderRequest request = new CreateOrderRequest();
            request.setOrderAmount(cashfreeDto.getAmount());
            request.setOrderCurrency("INR");
            request.setCustomerDetails(customerDetails);
            Cashfree cashfree = new Cashfree();
            ApiResponse<OrderEntity> response = cashfree.PGCreateOrder("2023-08-01", request, null, null, null);
            logger.info("Payment session ID generated successfully: {}", response.getData().getPaymentSessionId());
            result.put("res",response.getData());
            result.put("status" , 200);
        }
        catch (ApiException e){
            logger.error("Exception occurred while getting payment session ID: {}", e.getMessage());
            result.put("message", "Something went wrong during getPaymentSessionId payment. please contact to administrator.");
            result.put("status",500);
            logger.info( "Exception occur in  getPaymentSessionId :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @GetMapping("pay")
    public String redirectPaymentPage(@RequestBody CashfreeDto cashfreeDto,Model model) {
        logger.info("Redirecting to payment page for mobile number: {}", cashfreeDto.getMobileNumber());
        model.addAttribute("mobile",cashfreeDto.getMobileNumber());
        model.addAttribute("amount",cashfreeDto.getAmount());
        return "cashfree";
    }

}
