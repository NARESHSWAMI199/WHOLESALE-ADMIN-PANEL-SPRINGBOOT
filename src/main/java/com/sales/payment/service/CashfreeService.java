package com.sales.payment.service;


import com.cashfree.ApiException;
import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;
import com.cashfree.model.OrderMeta;
import com.sales.dto.CashfreeDto;
import com.sales.dto.CashfreeFilters;
import com.sales.entities.CashfreeTrans;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.payment.controller.CashFreePgController;
import com.sales.utils.Utils;
import com.sales.wholesaler.services.WholesaleServicePlanService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.sales.specifications.CashfreeSpecification.*;

@Service
public class CashfreeService extends PaymentRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(CashFreePgController.class);


    @Value("${cashfree.key}")
    String key;

    @Value("${cashfree.mid}")
    public String mid;

    @Value("${cashfree.mobile}")
    String mobileNumber;

    @Value("${cashfree.redirect_uri}")
    String redirectUri;

    @Value("${cashfree.callback_uri}")
    String callbackUri;

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

        int updatedRows =  cashfreeHbRepository.updateCashfreePaymentDetail(cashfreeDto,userId);
        logger.info("Updated rows in updateCashfreeCallback. return by updatePaymentCallback -> {}",updatedRows);
        // The Active plan is payment status is successful
        if(paymentStatus.equals("SUCCESS")) wholesaleServicePlanService.assignUserPlan(userId, servicePlanId);
        logger.info("Ended updateCashfreeCallback.");
        return updatedRows;
    }




    public OrderEntity getOrderEntityForCashfreePayment(CashfreeDto cashfreeDto, User loggedUser, ServicePlan servicePlan,String env) throws ApiException {
        logger.info("Started getOrderEntityForCashfreePayment with params : cashfreeDto : {} and loggedUser : {} and servicePlan : {} and env : {}",cashfreeDto.toString(),loggedUser.toString(),servicePlan.toString(),env);
        long amount = (servicePlan.getPrice()-servicePlan.getDiscount());
        String slug = UUID.randomUUID().toString();
        logger.info("amount {}",amount);
        Cashfree.XClientId = mid;
        Cashfree.XClientSecret = key;
        if(env != null && env.equalsIgnoreCase("TEST")){
            Cashfree.XEnvironment = Cashfree.SANDBOX;
        }else {
            Cashfree.XEnvironment = Cashfree.PRODUCTION;
        }
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerId(UUID.randomUUID().toString());
        customerDetails.setCustomerPhone(mobileNumber);

        CreateOrderRequest request = new CreateOrderRequest();
        OrderMeta orderMeta = new OrderMeta();
        orderMeta.setReturnUrl(redirectUri);
        orderMeta.setNotifyUrl(callbackUri+"/cashfree/callback/"+slug+"/"+loggedUser.getId()+"/"+servicePlan.getId());
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
        cashfreeDto.setUserId(loggedUser.getId());
        insertPaymentDetail(cashfreeDto);
        logger.info("Ended getOrderEntityForCashfreePayment with -> {}",response.getData());
        return response.getData();
    }




}
