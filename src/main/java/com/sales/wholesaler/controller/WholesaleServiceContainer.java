package com.sales.wholesaler.controller;

import com.sales.admin.services.AddressService;
import com.sales.admin.services.ServicePlanService;
import com.sales.admin.services.WalletService;
import com.sales.jwtUtils.JwtToken;
import com.sales.utils.ReadExcel;
import com.sales.wholesaler.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WholesaleServiceContainer {
    @Autowired
    protected WholesaleStoreService wholesaleStoreService;

    @Autowired
    protected WholesaleUserService wholesaleUserService;

    @Autowired
    protected WholesaleItemService wholesaleItemService;

    @Autowired
    protected AddressService addressService;


    @Autowired
    protected WholesaleItemReviewService wholesaleItemReviewService;

    @Autowired
    protected WholesalePromotionsService wholesalePromotionsService;

    @Autowired
    protected WholesaleServicePlanService wholesaleServicePlanService;

    @Autowired
    protected ServicePlanService servicePlanService;

    @Autowired
    protected JwtToken jwtToken;

    @Autowired
    protected ChatService chatService;

    @Autowired
    protected ChatUserService chatUserService;

    @Autowired
    protected ContactsService contactService;

    @Autowired
    protected BlockListService blockListService;

    @Autowired
    protected WholesalePaginationService wholesalePaginationService;

    @Autowired
    protected ReadExcel readExcel;

    @Autowired
    protected ChatRoomService chatRoomService;

    @Autowired
    protected WholesaleFuturePlansService wholesaleFuturePlansService;

    @Autowired
    protected WalletTransactionService walletTransactionService;

    @Autowired
    protected WholesaleWalletService wholesaleWalletService;

    @Autowired
    protected WalletService walletService;

}
