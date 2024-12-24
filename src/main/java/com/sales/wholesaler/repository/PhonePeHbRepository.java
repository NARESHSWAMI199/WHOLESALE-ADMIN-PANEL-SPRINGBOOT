package com.sales.wholesaler.repository;


import com.sales.entities.PhonePeTrans;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class PhonePeHbRepository {


    @Autowired
    EntityManager entityManager;

    public int updatePhonePeTransaction(PhonePeTrans phonePeTrans){
        String hql = "update PhonePeTrans set " +
                "xVerify = :xVerify,"+
                "transactionId = :transactionId, " +
                "bankId=:bankId, " +
                "responseCode = :responseCode," +
                "paymentType = :paymentType,"+
                "state = :state," +
                "code = :code," +
                "message = :message," +
                "actualResponse = :actualResponse," +
                "status =:status " +
                "where id = :id";
        Query query = entityManager.createQuery(hql);
        query.setParameter("xVerify",phonePeTrans.getXVerify());
        query.setParameter("transactionId",phonePeTrans.getTransactionId());
        query.setParameter("bankId",phonePeTrans.getBankId());
        query.setParameter("responseCode",phonePeTrans.getResponseCode());
        query.setParameter("paymentType",phonePeTrans.getPaymentType());
        query.setParameter("state",phonePeTrans.getState());
        query.setParameter("code",phonePeTrans.getCode());
        query.setParameter("message",phonePeTrans.getMessage());
        query.setParameter("actualResponse",phonePeTrans.getActualResponse());
        query.setParameter("status",phonePeTrans.getStatus());
        query.setParameter("id",phonePeTrans.getId());
        return query.executeUpdate();
    }


}
