package com.sales.admin.repositories;


import com.sales.dto.GroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class PermissionHbRepository {

    @Autowired
    EntityManager entityManager;


    public int updateGroup(GroupDto groupDto){
        String hql = "update Group set name=:name where slug = :slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("name", groupDto.getName());
        query.setParameter("slug",groupDto.getSlug());
        updatePermissions(groupDto.getGroupId(),groupDto.getPermissions());
        return query.executeUpdate();
    }


    public int updatePermissions(Integer groupId, List<Integer> permissions){
        String values = "";
        for(int i=0; i < permissions.size(); i++){
            values +="("+groupId+","+permissions.get(i)+")";
            if(i < permissions.size()-1) values += ",";
        }
        String hql = "insert into group_permissions (group_id,permission_id) values :values";
        Query query = entityManager.createQuery(hql);
        query.setParameter("values", values);
        return query.executeUpdate();
    }



}
