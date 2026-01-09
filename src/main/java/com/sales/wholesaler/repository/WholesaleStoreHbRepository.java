package com.sales.wholesaler.repository;

import com.sales.claims.AuthUser;
import com.sales.dto.StoreDto;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class WholesaleStoreHbRepository {

    private final EntityManager entityManager;

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

    public int updateStore(StoreDto storeDto, AuthUser loggedUser){
        String strQuery = "update Store set " +
                "storeName=:name , " +
                "email=:email, "+
                "avtar=:avtar, "+
                "phone=:phone, "+
                "storeCategory =:storeCategory,"+
                "storeSubCategory =:storeSubCategory,"+
                "description=:description, "+
                "updatedAt=:updatedAt, "+
                "updatedBy=:updatedBy "+
                "where slug =:slug";

        Query query = entityManager.createQuery(strQuery);
        query.setParameter("name", storeDto.getStoreName());
        query.setParameter("email", storeDto.getStoreEmail());
        query.setParameter("phone", storeDto.getStorePhone());
        query.setParameter("storeCategory", storeDto.getStoreCategory());
        query.setParameter("storeSubCategory", storeDto.getStoreSubCategory());
        query.setParameter("avtar", storeDto.getStoreAvatar());
        query.setParameter("description", storeDto.getDescription());
        query.setParameter("updatedAt", Utils.getCurrentMillis());
        query.setParameter("updatedBy", loggedUser.getId());
        query.setParameter("slug", storeDto.getStoreSlug());
        return query.executeUpdate();
    }



    public int updateSeenNotifications(long id){
        String strQuery = "update StoreNotifications set " +
                "seen='Y' " +
                "where id=:id";
        Query query = entityManager.createQuery(strQuery);
        query.setParameter("id", id);
        return query.executeUpdate();
    }


}
