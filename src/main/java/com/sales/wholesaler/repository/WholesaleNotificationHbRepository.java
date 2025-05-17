package com.sales.wholesaler.repository;


import com.sales.entities.StoreNotifications;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class WholesaleNotificationHbRepository {

    @Autowired
    EntityManager entityManager;


    public void insertStoreNotifications(StoreNotifications storeNotifications){
        String hql = "INSERT INTO store_notification " +
                "(wholesale_id,title, message_body, created_at, created_by, is_deleted, seen) " +
                "VALUES(:storeId,:title,:messageBody, :createAt, :createdBy, 'N', 'N')";
        Query query = entityManager.createNativeQuery(hql);
        query.setParameter("storeId", storeNotifications.getWholesaleId());
        query.setParameter("title", storeNotifications.getTitle());
        query.setParameter("messageBody", storeNotifications.getMessageBody());
        query.setParameter("createAt", Utils.getCurrentMillis());
        query.setParameter("createdBy", storeNotifications.getCreatedBy().getId());
        query.executeUpdate();
    }

}
