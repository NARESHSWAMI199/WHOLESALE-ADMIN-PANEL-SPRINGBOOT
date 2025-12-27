package com.sales.payment.service;

import com.sales.dto.SearchFilters;
import com.sales.helpers.SafeLogHelper;
import com.sales.payment.repository.CashfreeHbRepository;
import com.sales.payment.repository.CashfreeRepository;
import com.sales.payment.repository.PhonePeHbRepository;
import com.sales.payment.repository.PhonePeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepoContainer {
    private  final com.sales.helpers.Logger safeLog = SafeLogHelper.getInstance();
    private final Logger logger = LoggerFactory.getLogger(PaymentRepoContainer.class);

    @Autowired
    protected PhonePeRepository phonePeRepository;

    @Autowired
    protected PhonePeHbRepository phonePeHbRepository;

    @Autowired
    protected CashfreeRepository phonePeDto;

    @Autowired
    protected CashfreeRepository cashfreeRepository;

    @Autowired
    protected CashfreeHbRepository cashfreeHbRepository;

    public Pageable getPageable(SearchFilters filters){
        safeLog.info(logger,"page : {} {}", filters.getPageNumber(), filters.getSize());
        Sort sort = (filters.getOrder().equalsIgnoreCase("asc")) ?
                Sort.by(filters.getOrderBy()).ascending() :  Sort.by(filters.getOrderBy()).descending();
        Pageable pageable = PageRequest.of(filters.getPageNumber(), filters.getSize(),sort);
        return pageable;
    }

}
