package com.sales.chats.repositories;


import com.sales.entities.AuthUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class BlockListHbRepository {

    private final EntityManager entityManager;


    public boolean deleteUserFromBlockList(Integer userId, AuthUser blockedUser){
        String hql = "delete from BlockedUser where userId=:userId and blockedUser=:blockedUser";
        Query query = entityManager.createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("blockedUser",blockedUser);
        return query.executeUpdate() > 0;
    }



}
