package com.sales.wholesaler.services;


import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Component
@Transactional
public class WholesalerHbPermissionsRepository {
    @Autowired
    EntityManager entityManager;



        public int deleteWholesalerPermission(int userId){
            if (userId == GlobalConstant.suId) return  0;
            String sql = "delete from `user_groups` where user_id = :userId";
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
