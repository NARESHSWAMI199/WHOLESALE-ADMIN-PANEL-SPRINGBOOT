package com.sales.payment.service;


import com.sales.dto.CashfreeDto;
import com.sales.dto.CashfreeFilters;
import com.sales.entities.CashfreeTrans;
import com.sales.payment.controller.CashFreePgController;
import com.sales.utils.Utils;
import com.sales.wholesaler.services.WholesaleServicePlanService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.specifications.CashfreeSpecification.*;

@Service
public class CashfreeService extends PaymentRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(CashFreePgController.class);
    @Autowired
    WholesaleServicePlanService wholesaleServicePlanService;

    public Page<CashfreeTrans> getAllPaymentHistoryFromCashfree(CashfreeFilters cashfreeFilters) {
        Specification<CashfreeTrans> specification = Specification.where(
             hasCfPaymentId(cashfreeFilters.getTransactionId())
            .and( hasPaymentStatus(cashfreeFilters.getPaymentStatus()))
            .and(greaterThanOrEqualFromDate(cashfreeFilters.getFromDate()))
            .and(lessThanOrEqualToToDate(cashfreeFilters.getToDate()))
        );
        Pageable pageable = getPageable(cashfreeFilters);
        return cashfreeRepository.findAll(specification,pageable);
    }


    public void insertPaymentDetail(CashfreeDto cashfreeDto) {
        CashfreeTrans cashfreeTrans = CashfreeTrans.builder()
                .orderId(cashfreeDto.getOrderId())
                .amount(String.valueOf(cashfreeDto.getAmount()))
                .status(cashfreeDto.getStatus())
                .currency(cashfreeDto.getCurrency())
                .slug(cashfreeDto.getSlug())
                .amount(String.valueOf(cashfreeDto.getAmount()))
                .userId(cashfreeDto.getUserId())
                .createdAt(Utils.getCurrentMillis())
                .build();
        cashfreeRepository.save(cashfreeTrans);
    }


    public int updatePaymentCallback(CashfreeDto cashfreeDto, Integer userId){
        return cashfreeHbRepository.updateCashfreePaymentDetail(cashfreeDto,userId);
    }


    public int updateCashfreeCallback (JSONObject order,JSONObject payment,String slug,Integer userId,Integer servicePlanId,String paymentResponseStr) {
        logger.info("started updateCashfreeCallback with params order : {} payment : {} slug : {} userId : {} servicePlanId : {} ",order,payment,slug,userId,servicePlanId);
        String orderId = String.valueOf(order.get("order_id"));
        String cfPaymentId = String.valueOf(payment.get("cf_payment_id"));
        String paymentStatus = payment.getString("payment_status");
        String paymentAmount = String.valueOf(payment.get("payment_amount"));
        String paymentCurrency = payment.getString("payment_currency");
        String paymentMessage = payment.getString("payment_message");
        String paymentTime = String.valueOf(payment.get("payment_time"));
        String bankReference = String.valueOf(payment.get("bank_reference"));
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

        int updatedRows = updatePaymentCallback(cashfreeDto,userId);
        logger.info("Updated rows in updateCashfreeCallback. return by updatePaymentCallback -> {}",updatedRows);
        // The Active plan is payment status is successful
        if(paymentStatus.equals("SUCCESS")) wholesaleServicePlanService.assignUserPlan(userId, servicePlanId);
        logger.info("Ended updateCashfreeCallback.");
        return updatedRows;
    }




}
