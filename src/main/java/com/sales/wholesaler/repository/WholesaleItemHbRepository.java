package com.sales.wholesaler.repository;


import com.sales.dto.ItemDto;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Component
@Transactional
public class WholesaleItemHbRepository {

    @Autowired
    EntityManager entityManager;
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "price")
    float price;
    @Column(name = "discount")
    float discount;
    @Column(name = "description")
    String description;

    public int updateItems(ItemDto itemDto, User loggedUser){
        String hqQuery = "update Item set " +
                "name =:name," +
                "description =:description," +
                "price =:price," +
                "discount =:discount," +
                "avtar =:avtar,"+
                "itemCategory =:itemCategory,"+
                "updatedAt =:updatedAt," +
                "updatedBy =:updatedBy " +
                "where slug =:slug and wholesaleId =:wholesaleId";
        Query query = entityManager.createQuery(hqQuery);
        query.setParameter("name" , itemDto.getName());
        query.setParameter("description" , itemDto.getDescription());
        query.setParameter("price" , itemDto.getPrice());
        query.setParameter("discount" , itemDto.getDiscount());
        query.setParameter("avtar" , itemDto.getAvtar());
        query.setParameter("itemCategory" , itemDto.getItemCategory());
        query.setParameter("updatedAt" , Utils.getCurrentMillis());
        query.setParameter("updatedBy" , loggedUser.getId());
        query.setParameter("slug",itemDto.getSlug());
        query.setParameter("wholesaleId",itemDto.getStoreId());
        return  query.executeUpdate();
    }


    public int deleteItem(String slug,Integer storeId){
        String hqlString = "update Item set isDeleted='Y' where slug=:slug and wholesaleId =:wholesaleId";
        Query query = entityManager.createQuery(hqlString);
        query.setParameter("slug",slug);
        query.setParameter("wholesaleId",storeId);
        return query.executeUpdate();
    }

    public int updateStock(String stock , String slug, Integer storeId){
        String hqlString = "update Item set inStock=:stock where slug=:slug and wholesaleId =:wholesaleId";
        Query query = entityManager.createQuery(hqlString);
        query.setParameter("stock",stock);
        query.setParameter("slug",slug);
        query.setParameter("wholesaleId",storeId);
        return query.executeUpdate();
    }

    public int updateStatus(String slug, String status){
        String hql = "Update Item set status=:status where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("status",status);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }
    public int updateItemImage(String slug , String filename){
        String hql = "update Item set avtar =:avtar where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("avtar", filename);
        query.setParameter("slug", slug);
        return query.executeUpdate();
    }

}
