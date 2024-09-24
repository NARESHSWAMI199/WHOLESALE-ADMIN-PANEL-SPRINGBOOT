package com.sales.admin.repositories;


import com.sales.dto.GroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Component
public class PermissionHbRepository {

    @Autowired
    EntityManager entityManager;


    public int updateGroup(GroupDto groupDto){
        String hql = "update Group set name=:name where slug = :slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("name", groupDto.getName());
        query.setParameter("slug",groupDto.getSlug());
        return query.executeUpdate();
    }



}
