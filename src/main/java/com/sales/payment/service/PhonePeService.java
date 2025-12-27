package com.sales.payment.service;


import com.phonepe.sdk.pg.Env;
import com.phonepe.sdk.pg.common.http.PhonePeResponse;
import com.phonepe.sdk.pg.payments.v1.PhonePePaymentClient;
import com.phonepe.sdk.pg.payments.v1.models.request.PgRefundRequest;
import com.phonepe.sdk.pg.payments.v1.models.response.PgPaymentInstrument;
import com.phonepe.sdk.pg.payments.v1.models.response.PgTransactionStatusResponse;
import com.phonepe.sdk.pg.payments.v1.models.response.UPIPaymentInstrumentResponse;
import com.sales.dto.PhonePeDto;
import com.sales.entities.PhonePeTrans;
import com.sales.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PhonePeService extends PaymentRepoContainer {

  private final com.sales.helpers.Logger log;
  private static final Logger logger = LoggerFactory.getLogger(PhonePeService.class);

    @Value("${phonepe.key}")
    String saltKey;

    @Value("${phonepe.mid}")
    public String mid;

    @Value("${phonepe.env}")
    public String phonePeEnv;

    public PhonePePaymentClient getPhonePeClient() {
        log.info(logger,"Starting getPhonePeClient method");
        String merchentId = saltKey;
        String key = mid;
        Integer saltIndex = 1;
        Env env = Env.UAT;
        if (phonePeEnv.equals("PROD")) {
            env = Env.PROD;
        }
        boolean shouldPublishEvents = true;
        PhonePePaymentClient client = new PhonePePaymentClient(merchentId, key, saltIndex, env, shouldPublishEvents);
        log.info(logger,"Completed getPhonePeClient method");
        return client;
    }

    public PhonePeTrans savePhonePeTransaction(PhonePeDto phonePeDto) {
        log.info(logger,"Starting savePhonePeTransaction method");
        PhonePeTrans phonePeTrans = PhonePeTrans.builder()
            .merchantTransactionId(phonePeDto.getMerchantTransactionId())
            .userId(phonePeDto.getUserId())
            .amount(phonePeDto.getAmount())
            .status("P")
            .createdAt(Utils.getCurrentMillis())
            .build();
        PhonePeTrans savedTransaction = phonePeRepository.save(phonePeTrans); // Create operation
        log.info(logger,"Completed savePhonePeTransaction method");
        return savedTransaction;
    }

    public int updatePhonePeTransaction(PhonePeTrans phonePeTrans) {
        log.info(logger,"Starting updatePhonePeTransaction method");
        String responseCode = phonePeTrans.getResponseCode();
        if (responseCode.equalsIgnoreCase("SUCCESS")) {
            phonePeTrans.setStatus("S");
        } else if (responseCode.equalsIgnoreCase("ZU")) {
            phonePeTrans.setStatus("F");
        }
        int updateCount = phonePeHbRepository.updatePhonePeTransaction(phonePeTrans); // Update operation
        log.info(logger,"Completed updatePhonePeTransaction method");
        return updateCount;
    }

    public UPIPaymentInstrumentResponse checkUpiPaymentStatus(String merchantTransactionId) {
        log.info(logger,"Starting checkUpiPaymentStatus method");
        PhonePePaymentClient phonePePaymentClient = getPhonePeClient();
        PhonePeResponse<PgTransactionStatusResponse> statusResponse = phonePePaymentClient.checkStatus(merchantTransactionId);
        PgPaymentInstrument pgPaymentInstrument = statusResponse.getData().getPaymentInstrument();
        log.info(logger,"Completed checkUpiPaymentStatus method");
        return (UPIPaymentInstrumentResponse) pgPaymentInstrument;
    }

    public boolean checkValidityOfPaymentCallback(PhonePeDto phonePeDto, String mid) {
        log.info(logger,"Starting checkValidityOfPaymentCallback method");
        String xVerify = phonePeDto.getXVerify();
        String response = phonePeDto.getEncodedResponse();
        if (!Utils.isEmpty(response)) {
            if (response.contains("response")) {
                response = "{\"response\":\"" + response + "\"}";
            }
        }
        PhonePePaymentClient phonepeClient = getPhonePeClient();
        boolean isValid = phonepeClient.verifyResponse(xVerify, response);
        log.info(logger,"Completed checkValidityOfPaymentCallback method");
        return isValid;
    }

    public PhonePeResponse takeRefund(PhonePeDto phonePeDto, String notifyUrl) {
        log.info(logger,"Starting takeRefund method");
        PgRefundRequest pgRefundRequest = PgRefundRequest.builder()
            .amount(phonePeDto.getAmount() * 100)
            .callbackUrl(notifyUrl)
            .merchantId(mid)
            .merchantTransactionId(phonePeDto.getMerchantTransactionId())
            .originalTransactionId(phonePeDto.getMerchantTransactionId())
            .build();
        PhonePePaymentClient phonepeClient = getPhonePeClient();
        PhonePeResponse refundResponse = phonepeClient.refund(pgRefundRequest);
        log.info(logger,"Completed takeRefund method");
        return refundResponse;
    }

}
