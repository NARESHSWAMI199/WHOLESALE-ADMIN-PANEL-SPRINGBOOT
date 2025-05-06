package com.sales.payment.service;


import com.sales.dto.CashfreeDto;
import com.sales.dto.CashfreeFilters;
import com.sales.entities.CashfreeTrans;
import com.sales.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.specifications.CashfreeSpecification.*;

@Service
public class CashfreeService extends PaymentRepoContainer {


    public Page<CashfreeTrans> getAllPaymentHistoryFromCashfree(CashfreeFilters cashfreeFilters) {
        Specification<CashfreeTrans> specification = Specification.where(
             hasCfPaymentId(cashfreeFilters.getTransactionId())
            .and( hasPaymentStatus(cashfreeFilters.getPaymentStatus()))
            .and(greaterThanOrEqualFromDate(cashfreeFilters.getFromDate()))
            .and(lessThanOrEqualToToDate(cashfreeFilters.getToDate()))
        );
        Pageable pageable = getPageable(cashfreeFilters);
        return cashfreeRepository.findAll(specification,pageable);
    }


    public void insertPaymentDetail(CashfreeDto cashfreeDto) {
        CashfreeTrans cashfreeTrans = CashfreeTrans.builder()
                .orderId(cashfreeDto.getOrderId())
                .amount(String.valueOf(cashfreeDto.getAmount()))
                .status(cashfreeDto.getStatus())
                .currency(cashfreeDto.getCurrency())
                .slug(cashfreeDto.getSlug())
                .amount(String.valueOf(cashfreeDto.getAmount()))
                .userId(cashfreeDto.getUserId())
                .createdAt(Utils.getCurrentMillis())
                .build();
        cashfreeRepository.save(cashfreeTrans);
    }


    public int updatePaymentCallback(CashfreeDto cashfreeDto, Integer userId){
        return cashfreeHbRepository.updateCashfreePaymentDetail(cashfreeDto,userId);
    }

}
