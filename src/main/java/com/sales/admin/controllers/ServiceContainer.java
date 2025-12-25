package com.sales.admin.controllers;

import com.sales.admin.repositories.UserPaginationsRepository;
import com.sales.admin.services.*;
import com.sales.utils.ReadExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceContainer {
    @Autowired
    protected StoreService storeService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    ItemReviewService itemReviewService;

    @Autowired
    protected AddressService addressService;

    @Autowired
    protected  ReadExcel readExcel;

    @Autowired
    protected  GroupService groupService;

    @Autowired
    protected ServicePlanService servicePlanService;

    @Autowired
    protected PaginationService paginationService;

    @Autowired
    protected UserPaginationsRepository userPaginationsRepository;

    @Autowired
    protected StoreReportService storeReportService;

    @Autowired
    protected ItemReportService itemReportService;

    @Autowired
    protected WalletService walletService;

    @Autowired
    protected StoreWalletTransactionService storeWalletTransactionService;

}
