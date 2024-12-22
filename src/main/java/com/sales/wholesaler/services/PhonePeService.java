package com.sales.wholesaler.services;


import com.sales.dto.PhonePeDto;
import com.sales.entities.PhonePeTrans;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.PgPaymentInstrument;
import com.phonepe.sdk.pg.payments.v1.models.response.PgTransactionStatusResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.UPIPaymentInstrumentResponse;
import com.phonepe.sdk.pg.payments.v1.models.request.PgRefundRequest;


@Service
public class PhonePeService extends WholesaleRepoContainer {


    @Value("${phonepe.test.key}")
    String testSaltKey;

    @Value("${phonepe.test.mid}")
    public String testMid;

    @Value("${phonepe.prod.key}")
    String saltKey;

    @Value("${phonepe.prod.mid}")
    public String mid;

    @Value("${phonepe.env}")
    public String phonePeEnv;


    public PhonePePaymentClient getPhonePeClient(){
        String merchentId = testMid;
        String key = testSaltKey;
        Integer saltIndex = 1;
        Env env = Env.UAT;
        if(phonePeEnv.equals("PROD")){
            merchentId = mid;
            key = saltKey;
            env = Env.PROD;
        }
        System.err.println("mid : "+merchentId + " key : "+ key);
        boolean shouldPublishEvents = true;
        return new PhonePePaymentClient(merchentId, key, saltIndex, env, shouldPublishEvents);
    }


    public PhonePeTrans savePhonePeTransaction(PhonePeDto phonePeDto){
        PhonePeTrans phonePeTrans = PhonePeTrans.builder()
                .merchantTransactionId(phonePeDto.getMerchantTransactionId())
                .amount(phonePeDto.getAmount())
                .status("P")
                .createdAt(Utils.getCurrentMillis())
                .build();
        return phonePeRepository.save(phonePeTrans);
    }


    public int updatePhonePeTransaction(PhonePeTrans phonePeTrans){
        String responseCode = phonePeTrans.getResponseCode();
        if(responseCode.equalsIgnoreCase("SUCCESS")){
            phonePeTrans.setStatus("S");
        }else if(responseCode.equalsIgnoreCase("ZU")){
            phonePeTrans.setStatus("F");
        }
        return phonePeHbRepository.updatePhonePeTransaction(phonePeTrans);
    }


    public UPIPaymentInstrumentResponse checkUpiPaymentStatus(String merchantTransactionId){
        PhonePePaymentClient phonePePaymentClient = getPhonePeClient();
        PhonePeResponse<PgTransactionStatusResponse> statusResponse= phonePePaymentClient.checkStatus(merchantTransactionId);
        PgPaymentInstrument pgPaymentInstrument=statusResponse.getData().getPaymentInstrument();
        return (UPIPaymentInstrumentResponse)pgPaymentInstrument;
    }

    public boolean checkValidityOfPaymentCallback(PhonePeDto phonePeDto,String mid){
        String xVerify = phonePeDto.getXVerify();
        String response = phonePeDto.getEncodedResponse();
        if(!Utils.isEmpty(response)) {
            if(response.contains("response")){
                response = "{\"response\":\""+response+"\"}";
            }
        }
        PhonePePaymentClient phonepeClient = getPhonePeClient();
        return phonepeClient.verifyResponse(xVerify,response);
    }


    public PhonePeResponse takeRefund (PhonePeDto phonePeDto,String notifyUrl){
        /* TODO : Need to update notify url */
        PgRefundRequest pgRefundRequest=PgRefundRequest.builder()
                .amount(phonePeDto.getAmount()*100)
                .callbackUrl(notifyUrl)
                .merchantId(mid)
                .merchantTransactionId(phonePeDto.getMerchantTransactionId())
                .originalTransactionId(phonePeDto.getMerchantTransactionId())
                .build();
        PhonePePaymentClient phonepeClient = getPhonePeClient();
        return  phonepeClient.refund(pgRefundRequest);
    }


}
