package com.sales.payment.repository;


import com.sales.dto.CashfreeDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class CashfreeHbRepository {

    private final EntityManager entityManager;

    public int updateCashfreePaymentDetail(CashfreeDto cashfreeDto, Integer userId){
        String hql = """
            update CashfreeTrans
            set
                userId = :userId,
                orderId = :orderId,
                amount = :amount,
                cfPaymentId = :cfPaymentId,
                status = :status,
                currency = :currency,
                message = :message,
                paymentTime = :paymentTime,
                paymentType = :paymentType,
                paymentMethod = :paymentMethod,
                actualResponse = :actualResponse
            where slug = :slug
        """;
        Query query = entityManager.createQuery(hql);
        query.setParameter("userId",String.valueOf(userId));
        query.setParameter("orderId",String.valueOf(cashfreeDto.getOrderId()));
        query.setParameter("amount",String.valueOf(cashfreeDto.getAmount()));
        query.setParameter("cfPaymentId",cashfreeDto.getCfPaymentId());
        query.setParameter("status",cashfreeDto.getStatus());
        query.setParameter("currency",cashfreeDto.getCurrency());
        query.setParameter("message",cashfreeDto.getMessage());
        query.setParameter("paymentTime",cashfreeDto.getPaymentTime());
        query.setParameter("paymentType",cashfreeDto.getPaymentType());
        query.setParameter("paymentMethod",cashfreeDto.getPaymentMethod());
        query.setParameter("actualResponse",cashfreeDto.getActualResponse());
        query.setParameter("slug",cashfreeDto.getSlug());
        return query.executeUpdate();
    }


}
