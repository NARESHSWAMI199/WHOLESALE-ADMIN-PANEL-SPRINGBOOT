package com.sales.admin.controllers;

import com.sales.admin.services.*;
import com.sales.utils.ReadExcel;
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
    ReadExcel readExcel;

    @Autowired
    GroupService groupService;



}
