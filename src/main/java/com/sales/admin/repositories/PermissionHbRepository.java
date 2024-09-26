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


    public int updateGroup(GroupDto groupDto,int groupId){
        deleteGroupPermissionByGroupId(groupId);
        String hql = "update Group set name=:name where slug = :slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("name", groupDto.getName());
        query.setParameter("slug",groupDto.getSlug());
        updatePermissions(groupId,groupDto.getPermissions());
        return query.executeUpdate();
    }


    public int updatePermissions(int groupId, List<Integer> permissions){
        String values = "";
        for(int i=0; i < permissions.size(); i++){
            values +="("+groupId+","+permissions.get(i)+")";
            if(i < permissions.size()-1) values += ",";
        }
        System.out.println(values);
        String sql = "insert into group_permissions (group_id,permission_id) values "+values;
        Query query = entityManager.createNativeQuery(sql);
        return query.executeUpdate();
    }


    public int deleteGroupBySlug(String slug){
        String sql = "delete from `groups` where slug=:slug";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int deleteGroupPermissionByGroupId(int groupId){
        String sql = "delete from `group_permissions` where group_id = :groupId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("groupId",groupId);
        return query.executeUpdate();
    }


}
