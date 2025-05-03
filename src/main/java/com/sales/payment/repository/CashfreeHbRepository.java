package com.sales.payment.repository;


import com.sales.dto.CashfreeDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashfreeHbRepository {

    @Autowired
    EntityManager entityManager;

    public int updateCashfreePaymentDetail(CashfreeDto cashfreeDto){
        String hql = """
        update CashfreeTrans 
            set amount =:amount,
            set cfPaymentId=:cfPaymentId,
            set status=:status,
            set currency =:currency,
            set message =:message,
            set paymentTime=:paymentTime,
            set paymentType=:paymentType,
            set paymentMethod=:paymentMethod,
            set actualResponse=:actualResponse
        where slug=:slug
        """;
        Query query = entityManager.createQuery(hql);
        query.setParameter("amount",cashfreeDto.getAmount());
        query.setParameter("cfPaymentId",cashfreeDto.getCfPaymentId());
        query.setParameter("status",cashfreeDto.getCfPaymentId());
        query.setParameter("currency",cashfreeDto.getCfPaymentId());
        query.setParameter("message",cashfreeDto.getCfPaymentId());
        query.setParameter("paymentTime",cashfreeDto.getCfPaymentId());
        query.setParameter("paymentType",cashfreeDto.getCfPaymentId());
        query.setParameter("paymentMethod",cashfreeDto.getCfPaymentId());
        query.setParameter("actualResponse",cashfreeDto.getCfPaymentId());
        query.setParameter("slug",cashfreeDto.getSlug());
        return query.executeUpdate();
    }


}
