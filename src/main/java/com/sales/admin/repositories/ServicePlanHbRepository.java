package com.sales.admin.repositories;

import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServicePlanHbRepository {

    @Autowired
    public EntityManager entityManager;

    public int updateServicePlansStatus(String status, String slug, User loggedUser){
        String hql = "update ServicePlan set status =:status ,updatedAt=:updatedAt, updatedBy=:updatedBy where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter(ConstantResponseKeys.STATUS,status);
        query.setParameter("updatedAt", Utils.getCurrentMillis());
        query.setParameter("updatedBy",loggedUser.getId());
        query.setParameter("slug", slug);
        return query.executeUpdate();
    }

    public int deleteServicePlan(String slug, User loggedUser){
        String hql = "update ServicePlan set isDeleted='Y',updatedAt=:updatedAt, updatedBy=:updatedBy where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("updatedAt", Utils.getCurrentMillis());
        query.setParameter("updatedBy",loggedUser.getId());
        query.setParameter("slug", slug);
        return query.executeUpdate();
    }


}
