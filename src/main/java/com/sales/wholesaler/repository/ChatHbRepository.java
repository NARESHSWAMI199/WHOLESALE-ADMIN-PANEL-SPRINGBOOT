package com.sales.wholesaler.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ChatHbRepository {

    @Autowired
    EntityManager entityManager;

    public boolean updateMessageToSent(long id){
        String hql = "update Chat set isSent='S' where id =:id ";
        Query query = entityManager.createQuery(hql);
        query.setParameter("id",id);
        return query.executeUpdate() > 0;
    }


}
