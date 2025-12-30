package com.sales.admin.services;


import com.sales.admin.repositories.*;
import com.sales.dto.SearchFilters;
import com.sales.utils.WriteExcelUtil;
import com.sales.wholesaler.repository.ChatHbRepository;
import com.sales.wholesaler.repository.ChatRepository;
import com.sales.wholesaler.repository.ChatUserRepository;
import com.sales.wholesaler.repository.ContactRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class RepoContainer {

    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(RepoContainer.class);

    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected UserHbRepository userHbRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    ItemReviewRepository itemReviewRepository;

    @Autowired
    protected ItemHbRepository itemHbRepository;

    @Autowired
    MeasurementUnitRepository measurementUnitRepository;
    @Autowired
    protected StoreHbRepository storeHbRepository;

    @Autowired
    protected AddressRepository addressRepository;

    @Autowired
    protected  AddressHbRepository addressHbRepository;

    @Autowired
    protected WriteExcelUtil writeExcel;


    @Autowired
    protected  GroupPermissionRepository groupPermissionRepository;

    @Autowired
    protected  PermissionHbRepository permissionHbRepository;


    @Autowired
    protected  GroupRepository groupRepository;

    @Autowired
    protected StorePermissionsRepository storePermissionsRepository;


    @Autowired
    protected ItemCategoryRepository itemCategoryRepository;

    @Autowired
    protected ItemSubCategoryRepository itemSubCategoryRepository;

    @Autowired
    protected StoreCategoryRepository storeCategoryRepository;

    @Autowired
    protected StoreSubCategoryRepository storeSubCategoryRepository;

    @Autowired
    protected StoreNotificationRepository storeNotificationRepository;

    @Autowired
    protected SupportEmailsRepository supportEmailsRepository;


    @Autowired
    protected ServicePlanRepository servicePlanRepository;


    @Autowired
    protected WholesalerPlansRepository wholesalerPlansRepository;

    @Autowired
    protected ServicePlanHbRepository servicePlanHbRepository;


    @Autowired
    protected ChatRepository chatRepository;

    @Autowired
    protected ContactRepository contactRepository;

    @Autowired
    protected ChatUserRepository chatUserRepository;

    @Autowired
    protected PaginationRepository paginationRepository;

    @Autowired
    protected UserPaginationsRepository userPaginationsRepository;

    @Autowired
    protected PaginationHbRepository paginationHbRepository;

    @Autowired
    protected StoreReportRepository storeReportRepository;

    @Autowired
    protected ItemReportRepository itemReportRepository;

    @Autowired
    protected ChatHbRepository chatHbRepository;

    @Autowired
    protected WalletRepository walletRepository;


    @Autowired
    protected StoreWalletTransactionRepository storeWalletTransactionRepository;

    public Pageable getPageable(SearchFilters filters){
        logger.debug("page : "+ filters.getPageNumber() + " "+filters.getSize());
        Sort sort = (filters.getOrder().equalsIgnoreCase("asc")) ?
                Sort.by(filters.getOrderBy()).ascending() :  Sort.by(filters.getOrderBy()).descending();
        Pageable pageable = PageRequest.of(filters.getPageNumber(), filters.getSize(),sort);
        return pageable;
    }



}
