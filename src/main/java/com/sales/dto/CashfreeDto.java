package com.sales.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CashfreeDto {
    String userSlug;
    String servicePlanSlug;
    String orderId;
    Double amount;
    String mobileNumber;
    String slug;
    String cfPaymentId;
    String status;
    String currency;
    String message;
    String paymentTime;
    String paymentType;
    String bankReference;
    String paymentMethod; // detailed description about payment
    String actualResponse;
    Long createdAt;
    Integer userId;



}
