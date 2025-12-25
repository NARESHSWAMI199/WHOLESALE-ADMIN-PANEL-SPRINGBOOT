package com.sales.payment.service;

import com.sales.dto.SearchFilters;
import com.sales.payment.repository.CashfreeHbRepository;
import com.sales.payment.repository.CashfreeRepository;
import com.sales.payment.repository.PhonePeHbRepository;
import com.sales.payment.repository.PhonePeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentRepoContainer {

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
        log.info("page : {} {}", filters.getPageNumber(), filters.getSize());
        Sort sort = (filters.getOrder().equalsIgnoreCase("asc")) ?
                Sort.by(filters.getOrderBy()).ascending() :  Sort.by(filters.getOrderBy()).descending();
        Pageable pageable = PageRequest.of(filters.getPageNumber(), filters.getSize(),sort);
        return pageable;
    }

}
