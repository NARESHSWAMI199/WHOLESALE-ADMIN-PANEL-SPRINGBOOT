package com.sales.payment.controller;


import com.sales.admin.services.ServicePlanService;
import com.sales.jwtUtils.JwtToken;
import com.sales.payment.service.CashfreeService;
import com.sales.payment.service.PhonePeService;
import com.sales.wholesaler.services.WalletTransactionService;
import com.sales.wholesaler.services.WholesaleServicePlanService;
import com.sales.wholesaler.services.WholesaleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceContainer {

    @Autowired
    protected PhonePeService phonePeService;
    @Autowired
    protected ServicePlanService servicePlanService;

    @Autowired
    protected JwtToken jwtToken;

    @Autowired
    protected WholesaleUserService wholesaleUserService;

    @Autowired
    protected WholesaleServicePlanService wholesaleServicePlanService;

    @Autowired
    protected CashfreeService cashfreeService;

    @Autowired
    protected WalletTransactionService walletTransactionService;

}
