package com.sales.wholesaler.controller;

import com.sales.admin.services.AddressService;
import com.sales.admin.services.ServicePlanService;
import com.sales.wholesaler.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Component
public class WholesaleServiceContainer {
    @Autowired
    protected WholesaleStoreService wholesaleStoreService;

    @Autowired
    protected WholesaleUserService wholesaleUserService;

    @Autowired
    protected WholesaleItemService wholesaleItemService;

    @Autowired
    Logger logger;

    @Autowired
    protected AddressService addressService;


    @Autowired
    protected WholesaleItemCommentService wholesaleItemCommentService;

    @Autowired
    protected WholesalePromotionsService wholesalePromotionsService;


    @Autowired
    protected  PhonePeService phonePeService;

    @Autowired
    protected WholesaleServicePlanService wholesaleServicePlanService;

    @Autowired
    protected ServicePlanService servicePlanService;

}
