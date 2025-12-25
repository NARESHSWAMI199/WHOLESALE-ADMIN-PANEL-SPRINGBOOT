package com.sales.admin.repositories;


import com.sales.dto.GroupDto;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
public class PermissionHbRepository {

    @Autowired
    EntityManager entityManager;


    public int updateGroup(GroupDto groupDto,int groupId){
        // deleting all group's exists permissions
        deleteGroupPermissionByGroupId(groupId);

        String hql = "update Group set name=:name where slug = :slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("name", groupDto.getName());
        query.setParameter("slug",groupDto.getSlug());

        // update permissions if there provided
        List<Integer> permissions = groupDto.getPermissions();
        if(permissions !=null && permissions.size() > 0) updatePermissions(groupId,permissions);
        return query.executeUpdate();
    }


    public int updatePermissions(int groupId, List<Integer> permissions){
        if(permissions ==null || permissions.isEmpty()) return 0;
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


    public int deleteGroupBySlug(String slug, int groupId){
        if (groupId == GlobalConstant.groupId) throw new PermissionDeniedDataAccessException("We can't delete this group.",null);
        deleteGroupPermissionByGroupId(groupId);
        deleteGroupFromUser(groupId);
        String sql = "delete from `groups` where slug=:slug";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int deleteGroupPermissionByGroupId(int groupId){
        // If group is a super group do nothing.
        if (groupId == GlobalConstant.groupId) return  0;
        String sql = "delete from `group_permissions` where group_id = :groupId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("groupId",groupId);
        return query.executeUpdate();
    }

    public int deleteGroupFromUser(int groupId){
        // If group is a super group do nothing.
        if (groupId == GlobalConstant.groupId) return  0;
        String sql = "delete from `user_groups` where group_id = :groupId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("groupId",groupId);
        return query.executeUpdate();
    }


    public int deleteUserGroups(int userId){
        // If user is a super admin do nothing.
        if (userId == GlobalConstant.suId) return  0;
        String sql = "delete from `user_groups` where user_id = :userId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId",userId);
        return query.executeUpdate();
    }



    public int assignGroupsToUser(int userId, List<Integer> groups, User loggedUser) throws MyException {
        if(groups.contains(GlobalConstant.groupId) && loggedUser.getId() != GlobalConstant.suId) groups.remove((Integer) GlobalConstant.groupId);
        deleteUserGroups(userId);
        if(groups.isEmpty()) throw new MyException("Please provide at least one group.");
        String values = "";
        for(int i=0; i < groups.size(); i++){
            values +="("+userId+","+groups.get(i)+")";
            if(i < groups.size()-1) values += ",";
        }
        System.out.println(values);
        String sql = "insert into user_groups (user_id,group_id) values "+values;
        Query query = entityManager.createNativeQuery(sql);
        return query.executeUpdate();
    }



    /** permissions for wholesaler */
    public int deleteWholesalerPermission(int userId){
        if (userId == GlobalConstant.suId) return  0;
        String sql = "delete from `wholesaler_permissions` where user_id = :userId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId",userId);
        return query.executeUpdate();
    }

    public int assignPermissionsToWholesaler(int userId, List<Integer> permissions) throws MyException {
        if(permissions.contains(GlobalConstant.suId)) permissions.remove((Integer) GlobalConstant.suId);
        deleteWholesalerPermission(userId);
        if(permissions.isEmpty()) throw new MyException("Please provide at least one permission.");
        String values = "";
        for(int i=0; i < permissions.size(); i++){
            values +="("+userId+","+permissions.get(i)+")";
            if(i < permissions.size()-1) values += ",";
        }
        System.out.println(values);
        String sql = "insert into wholesaler_permissions (user_id,permission_id) values "+values;
        Query query = entityManager.createNativeQuery(sql);
        return query.executeUpdate();
    }

}
