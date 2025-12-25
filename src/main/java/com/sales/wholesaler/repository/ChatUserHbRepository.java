package com.sales.wholesaler.repository;


import com.sales.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ChatUserHbRepository {

    @Autowired
    EntityManager entityManager;


    public boolean updateAcceptStatus(Integer userId, User chatUser, String senderAcceptStatus){
        String hql = "update ChatUser set senderAcceptStatus=:senderAcceptStatus where userId=:userId and chatUser =:chatUser ";
        Query query = entityManager.createQuery(hql);
        query.setParameter("senderAcceptStatus",senderAcceptStatus);
        query.setParameter("userId",userId);
        query.setParameter("chatUser",chatUser);
        return  query.executeUpdate() > 0;
    }

}
