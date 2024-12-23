package com.sales.wholesaler.controller;


import com.google.gson.Gson;
import com.phonepe.sdk.pg.common.http.PhonePeException;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import com.sales.dto.PhonePeDto;
import com.sales.entities.PhonePeTrans;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v1.models.request.PgPayRequest;
import com.phonepe.sdk.pg.payments.v1.models.response.PayPageInstrumentResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.PgPayResponse;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("pg")
public class PhonePeGatewayController extends WholesaleServiceContainer {

    @ResponseBody
    @PostMapping("pay")
    public ResponseEntity<Map<String,Object>> payViaPhonePe(@RequestBody PhonePeDto phonePeDto){
        Map<String,Object> result = new HashMap<>();
        try {
            String merchantTransactionId = UUID.randomUUID().toString().substring(0, 34);
            logger.info("merchantTransactionId : "+merchantTransactionId);
            long amount = phonePeDto.getAmount()*100; /* converting in rupees */
            String merchantUserId = UUID.randomUUID().toString().substring(0, 34);
            String callbackUrl = "http://localhost:8080/pg/callback";
            String redirectUrl = "http://localhost:8080/pg/home";
            phonePeDto.setMerchantTransactionId(merchantTransactionId);
            phonePeDto.setAmount(amount);
            PhonePeTrans phonePeTrans =  phonePeService.savePhonePeTransaction(phonePeDto);
            if(phonePeTrans !=null){
                callbackUrl += "/"+phonePeTrans.getId();
            }
            String redirectMode = "REDIRECT";
            PgPayRequest pgPayRequest = PgPayRequest.PayPagePayRequestBuilder()
                    .amount(amount)
                    .merchantId(phonePeService.mid) /* Make sure must change mid according env */
                    .merchantTransactionId(merchantTransactionId)
                    .callbackUrl(callbackUrl)
                    .merchantUserId(merchantUserId)
                    .redirectUrl(redirectUrl)
                    .redirectMode(redirectMode)
                    .build();
            PhonePePaymentClient phonepeClient = phonePeService.getPhonePeClient();
            PhonePeResponse<PgPayResponse> payResponse = phonepeClient.pay(pgPayRequest);
            PayPageInstrumentResponse payPageInstrumentResponse = (PayPageInstrumentResponse) payResponse.getData().getInstrumentResponse();
            String url = payPageInstrumentResponse.getRedirectInfo().getUrl();
            result.put("res",payResponse);
            result.put("url",url);
            result.put("status" , 200);
        }
        catch (Exception e){
            result.put("message", "Something went wrong during payment. please contact to administrator.");
            result.put("status",500);
            logger.info( "Exception occur in  payViaPhonePe :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @RequestMapping("callback/{id}")
    public ResponseEntity<Map<String,Object>>  phonePeCallbackResponse(HttpServletRequest request,@PathVariable Integer id,@RequestBody Map<String,Object> paramsBody){

        Map<String,Object> result = new HashMap<>();
        try {
            String xVerify = request.getHeader("x_verify");
            String encodedResponseStr = (String) paramsBody.get("response");
            String decodedString = new String(Base64.getDecoder().decode(encodedResponseStr));

            JSONObject responseData = new JSONObject(decodedString);
            String code = responseData.getString("code");
            String message = responseData.getString("message");

            JSONObject data = responseData.getJSONObject("data");
            String transactionId = data.getString("transactionId");
            String responseCode = data.getString("responseCode");
            String state = data.getString("state");

            String paymentType = null;
            String bankId = null;
            if (!code.equals("PAYMENT_ERROR")) {
                JSONObject paymentInstrument = data.getJSONObject("paymentInstrument");
                paymentType = paymentInstrument.getString("type");
                bankId = paymentInstrument.has("bankId") ? paymentInstrument.getString("bankId") : "UNKNOWN" ;
            }
            PhonePeTrans phonePeTrans = PhonePeTrans.builder()
                    .id(id)
                    .xVerify(xVerify)
                    .bankId(bankId)
                    .responseCode(responseCode)
                    .paymentType(paymentType)
                    .state(state)
                    .code(code)
                    .actualResponse(encodedResponseStr)
                    .message(message)
                    .transactionId(transactionId)
                    .build();

            int isUpdated = phonePeService.updatePhonePeTransaction(phonePeTrans);
            result.put("isUpdate", isUpdated > 0);
            result.put("data", new Gson().fromJson(decodedString,Map.class));
            result.put("status",200);
        }  catch (Exception e){
            result.put("message", "Something went wrong during phonepe callback. please contact to administrator.");
            result.put("status",500);
            logger.info( "Exception occur in  phonePeCallbackResponse :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("refund")
    public ResponseEntity<Map<String,Object>> getRefund(@RequestBody  PhonePeDto phonePeDto){
        Map<String,Object> result = new HashMap<>();
        try{
            String notifyUrl = "http//:localhost:8080/pg/refund-notify";
            PhonePeResponse phonePeResponse = phonePeService.takeRefund(phonePeDto,notifyUrl);
            result.put("data", phonePeResponse);
            result.put("status",200);
        } catch (PhonePeException e){
            result.put("message", "Something went wrong during phonepe callback. please contact to administrator.");
            result.put("status",500);
            logger.info( "Exception occur in  getRefund :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @GetMapping("home")
    public String paymentRedirect(){
        return "hello";
    }

    @RequestMapping("refund-notify")
    public ResponseEntity<Map<String,Object>> getNotificationCallback(@RequestBody Map<String,Object> body){
        return new ResponseEntity<>(body,HttpStatus.OK);
    }


    @GetMapping("phonepe")
    public String phonePe(){
        return "phonepe";
    }



}
