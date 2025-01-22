package com.sales.admin.controllers;

import com.sales.admin.services.*;
import com.sales.utils.ReadExcel;
import com.sales.wholesaler.services.ChatUserService;
import com.sales.wholesaler.services.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ServiceContainer {
    @Autowired
    protected StoreService storeService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    ItemCommentService itemCommentService;

    @Autowired
    protected AddressService addressService;
    @Autowired
    Logger logger;

    @Autowired
    protected  ReadExcel readExcel;

    @Autowired
    protected  GroupService groupService;

    @Autowired
    protected ServicePlanService servicePlanService;

}
