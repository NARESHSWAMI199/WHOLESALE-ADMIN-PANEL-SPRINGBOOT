package com.sales.wholesaler.controller;

import com.sales.admin.services.AddressService;
import com.sales.wholesaler.services.WholesaleItemCommentService;
import com.sales.wholesaler.services.WholesaleItemService;
import com.sales.wholesaler.services.WholesaleStoreService;
import com.sales.wholesaler.services.WholesaleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


}
