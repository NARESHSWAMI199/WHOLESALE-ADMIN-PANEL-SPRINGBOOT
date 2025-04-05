package com.sales.wholesaler.repository;


import com.sales.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class BlockListHbRepository {
    @Autowired
    EntityManager entityManager;


    public boolean deleteUserFromBlockList(Integer userId,User blockedUser){
        String hql = "delete from BlockedUser where userId=:userId and blockedUser=:blockedUser";
        Query query = entityManager.createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("blockedUser",blockedUser);
        return query.executeUpdate() > 0;
    }



}
