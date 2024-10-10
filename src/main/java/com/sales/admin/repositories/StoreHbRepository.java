package com.sales.admin.repositories;

import com.sales.dto.StoreDto;
import com.sales.entities.StoreNotifications;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Component
@Transactional
public class StoreHbRepository {

    @Autowired
    EntityManager entityManager;

    public int deleteStore(String slug){
        String hql = "Update Store set isDeleted='Y' where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }


    public int deleteStore(int userId){
        String sql = "Update store set is_deleted='Y' where user_id=:userId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId",userId);
        return query.executeUpdate();
    }




    public int updateStatus(String slug, String status){
        String hql = "Update Store set status=:status where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("status",status);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int updateStore(StoreDto storeDto, User loggedUser){
        String strQuery = "update Store set " +
                "storeName=:name , " +
                "email=:email, "+
                "phone=:phone, "+
                "rating=:rating, "+
              //  "avtar=:avtar, "+
                "description=:description, "+
                "updatedAt=:updatedAt, "+
                "updatedBy=:updatedBy "+
                "where slug =:slug";

        Query query = entityManager.createQuery(strQuery);
        query.setParameter("name", storeDto.getStoreName());
        query.setParameter("email", storeDto.getStoreEmail());
        query.setParameter("phone", storeDto.getStorePhone());
        query.setParameter("rating", storeDto.getRating());
      //  query.setParameter("avtar", storeDto.getStoreAvatar());
        query.setParameter("description", storeDto.getDescription());
        query.setParameter("updatedAt", Utils.getCurrentMillis());
        query.setParameter("updatedBy", loggedUser.getId());
        query.setParameter("slug", storeDto.getStoreSlug());
        return query.executeUpdate();
    }


    public int updateStoreAvatar(String slug,String filename ){
        String hql = "update Store set avtar =:avtar where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("avtar", filename);
        query.setParameter("slug", slug);
        return query.executeUpdate();
    }


    public void insertStoreNotifications(StoreNotifications storeNotifications){
        String hql = "INSERT INTO store_notification " +
                "(wholesale_id,title, message_body, created_at, created_by, is_deleted, seen) " +
                "VALUES(:storeId,:title,:messageBody, :createAt, :createdBy, 'N', 'N')";
        Query query = entityManager.createNativeQuery(hql);
        query.setParameter("storeId", storeNotifications.getWholesaleId());
        query.setParameter("title", storeNotifications.getTitle());
        query.setParameter("messageBody", storeNotifications.getMessageBody());
        query.setParameter("createAt", Utils.getCurrentMillis());
        query.setParameter("createdBy", storeNotifications.getCreatedBy().getId());
        query.executeUpdate();
    }

}
