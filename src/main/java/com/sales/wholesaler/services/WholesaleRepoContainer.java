package com.sales.wholesaler.services;


import com.sales.admin.repositories.AddressHbRepository;
import com.sales.admin.repositories.AddressRepository;
import com.sales.dto.SearchFilters;
import com.sales.wholesaler.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class WholesaleRepoContainer {

    @Autowired
    protected WholesaleStoreRepository wholesaleStoreRepository;
    @Autowired
    protected WholesaleStoreHbRepository wholesaleStoreHbRepository;
    @Autowired
    protected WholesaleUserRepository wholesaleUserRepository;
    @Autowired
    protected WholesaleUserHbRepository wholesaleUserHbRepository;
    @Autowired
    protected WholesaleItemRepository wholesaleItemRepository;
    @Autowired
    protected WholesaleItemHbRepository wholesaleItemHbRepository;
    @Autowired
    protected WholesaleAddressRepository wholesaleAddressRepository;
    @Autowired
    protected  WholesaleAddressHbRepository wholesaleAddressHbRepository;


    /** same for both admin and wholesaler*/
    @Autowired
    protected AddressHbRepository addressHbRepository;

    @Autowired
    protected AddressRepository addressRepository;


    @Autowired
    protected WholesaleItemCategoryRepository wholesaleItemCategoryRepository;

    @Autowired
    protected WholesaleItemSubCategoryRepository wholesaleItemSubCategoryRepository;

    @Autowired
    WholesaleCategoryRepository wholesaleCategoryRepository;

    @Autowired
    protected WholesaleSubCategoryRepository wholesaleSubCategoryRepository;

    @Autowired
    protected WholesaleItemCommentRepository wholesaleItemCommentRepository;


    @Autowired
    protected WholesaleNotificationRepository wholesaleNotificationRepository;

    @Autowired
    protected WholesaleHbPromotion wholesaleHbPromotion;

    @Autowired
    protected WholesaleSupportEmailsRepository wholesaleSupportEmailsRepository;

    public Pageable getPageable(SearchFilters filters){
        System.err.println("page : "+ filters.getPageNumber() + " "+filters.getSize());
        Sort sort = (filters.getOrder().equalsIgnoreCase("asc")) ?
                Sort.by(filters.getOrderBy()).ascending() :  Sort.by(filters.getOrderBy()).descending();
        Pageable pageable = PageRequest.of(filters.getPageNumber(), filters.getSize(),sort);
        return pageable;
    }



}
