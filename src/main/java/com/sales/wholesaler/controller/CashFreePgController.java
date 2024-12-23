package com.sales.wholesaler.controller;


import com.cashfree.ApiException;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;
import com.sales.dto.CashfreeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cashfree.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("cashfree")
public class CashFreePgController extends WholesaleServiceContainer {

    @Value("${cashfree.test.key}")
    String testSaltKey;

    @Value("${cashfree.test.mid}")
    public String testMid;
    @ResponseBody
    @PostMapping("sessionId")
    public ResponseEntity<Map<String,Object>> getPaymentSessionId (@RequestBody CashfreeDto cashfreeDto) {
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
            System.err.println(response.getData().getPaymentSessionId());
            result.put("res",response.getData());
            result.put("status" , 200);
        }
        catch (ApiException e){
            result.put("message", "Something went wrong during getPaymentSessionId payment. please contact to administrator.");
            result.put("status",500);
            logger.info( "Exception occur in  getPaymentSessionId :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @GetMapping("pay")
    public String redirectPaymentPage(@RequestBody CashfreeDto cashfreeDto,Model model) {
        model.addAttribute("mobile",cashfreeDto.getMobileNumber());
        model.addAttribute("amount",cashfreeDto.getAmount());
        return "cashfree";
    }

}