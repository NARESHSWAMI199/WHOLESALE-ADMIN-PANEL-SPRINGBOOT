package com.sales.payment.controller;


import com.google.gson.Gson;
import com.phonepe.sdk.pg.common.http.PhonePeException;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import com.phonepe.sdk.pg.payments.v1.models.request.PgPayRequest;
import com.phonepe.sdk.pg.payments.v1.models.response.PayPageInstrumentResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.PgPayResponse;
import com.sales.dto.PhonePeDto;
import com.sales.entities.PhonePeTrans;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping("pg/")
public class PhonePeGatewayController extends PaymentServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(PhonePeGatewayController.class);

    @ResponseBody
    @GetMapping("pay/{slug}")
    public ResponseEntity<Map<String,Object>> payViaPhonePe(HttpServletRequest request,@PathVariable String slug){
        User loggedUser = Utils.getUserFromRequest(request,jwtToken,wholesaleUserService);
        ServicePlan servicePlan = servicePlanService.findBySlug(slug);
        logger.info("Initiating payment via PhonePe for user: {}, service plan: {}", loggedUser.getId(), servicePlan.getId());
        Map<String,Object> result = new HashMap<>();
        try {
            logger.info("user id  : {}",loggedUser.getId());
            String merchantTransactionId = UUID.randomUUID().toString().substring(0, 34);
            logger.info("merchantTransactionId : {}",merchantTransactionId);
            /* TODO : Must add Extra GST amount */
            long amount = (servicePlan.getPrice()-servicePlan.getDiscount())*100; /* converting in rupees */
            String merchantUserId = UUID.randomUUID().toString().substring(0, 34);
            String callbackUrl = "http://localhost:8080/pg/callback/"+servicePlan.getId() + "/"+loggedUser.getId();
            String redirectUrl = "http://localhost:8080/pg/home";
            PhonePeDto phonePeDto = PhonePeDto.builder()
                    .userId(loggedUser.getId())
                    .merchantTransactionId(merchantTransactionId)
                    .amount(servicePlan.getPrice())
                    .build();
            PhonePeTrans phonePeTrans =  phonePeService.savePhonePeTransaction(phonePeDto);
            if(phonePeTrans !=null){
                callbackUrl += "/"+phonePeTrans.getId();
            }
            String redirectMode = "REDIRECT";
            String merchentId = phonePeService.mid;
            PgPayRequest pgPayRequest = PgPayRequest.PayPagePayRequestBuilder()
                    .amount(amount)
                    .merchantId(merchentId) /* Make sure must change mid according env */
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
            logger.info("Payment URL generated successfully for user: {}", loggedUser.getId());
            result.put("res",payResponse);
            result.put("url",url);
            result.put("status" , 200);
        }
        catch (Exception e){
            logger.error("Exception occurred during payment via PhonePe: {}", e.getMessage());
            result.put("message", "Something went wrong during payment. please contact to administrator.");
            result.put(ConstantResponseKeys.STATUS,500);
            logger.error("Exception occur in  payViaPhonePe :: {}", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @RequestMapping("callback/{servicePlanId}/{userId}/{id}")
    public ResponseEntity<Map<String,Object>>  phonePeCallbackResponse(HttpServletRequest request,@PathVariable(name = "servicePlanId")Integer servicePlanId, @PathVariable(name = "userId") Integer userId , @PathVariable( name = "id") Integer id, @RequestBody Map<String,Object> paramsBody){
        logger.info("Received PhonePe callback for user: {}, service plan: {}", userId, servicePlanId);
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
            wholesaleServicePlanService.assignOrAddFuturePlans(userId,servicePlanId);
            logger.info("PhonePe callback processed successfully for user: {}", userId);
            result.put("isUpdate", isUpdated > 0);
            result.put("data", new Gson().fromJson(decodedString,Map.class));
            result.put(ConstantResponseKeys.STATUS,200);
        }  catch (Exception e){
            logger.error("Exception occurred during PhonePe callback: {}", e.getMessage());
            result.put("message", "Something went wrong during phonepe callback. please contact to administrator.");
            result.put(ConstantResponseKeys.STATUS,500);
            logger.info( "Exception occur in  phonePeCallbackResponse :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("refund")
    public ResponseEntity<Map<String,Object>> getRefund(@RequestBody  PhonePeDto phonePeDto){
        logger.info("Initiating refund for transaction: {}", phonePeDto);
        Map<String,Object> result = new HashMap<>();
        try{
            String notifyUrl = "http//:localhost:8080/pg/refund-notify";
            PhonePeResponse phonePeResponse = phonePeService.takeRefund(phonePeDto,notifyUrl);
            logger.info("Refund processed successfully for transaction: {}", phonePeDto);
            result.put("data", phonePeResponse);
            result.put(ConstantResponseKeys.STATUS,200);
        } catch (PhonePeException e){
            logger.error("Exception occurred during refund: {}", e.getMessage());
            result.put("message", "Something went wrong during phonepe callback. please contact to administrator.");
            result.put(ConstantResponseKeys.STATUS,500);
            logger.info( "Exception occur in  getRefund :: "+ e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get("status")));
    }

    @RequestMapping("refund-notify")
    public ResponseEntity<Map<String,Object>> getNotificationCallback(@RequestBody Map<String,Object> body){
        logger.info("Received refund notification callback");
        return new ResponseEntity<>(body,HttpStatus.OK);
    }
}
