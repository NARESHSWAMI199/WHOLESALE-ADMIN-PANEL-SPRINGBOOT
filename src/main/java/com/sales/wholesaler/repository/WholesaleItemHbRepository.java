package com.sales.wholesaler.repository;


import com.sales.claims.AuthUser;
import com.sales.dto.ItemDto;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Transactional
@RequiredArgsConstructor
public class WholesaleItemHbRepository {

    private final EntityManager entityManager;


    public int updateItems(ItemDto itemDto, AuthUser loggedUser){
        String hqQuery = "update Item set " +
                "name =:name," +
                "capacity =:capacity," +
                "description =:description," +
                "price =:price," +
                "discount =:discount," +
                "itemCategory =:itemCategory,"+
                "itemSubCategory =:itemSubCategory,"+
                "updatedAt =:updatedAt," +
                "updatedBy =:updatedBy " +
                "where slug =:slug and wholesaleId =:wholesaleId";
        Query query = entityManager.createQuery(hqQuery);
        query.setParameter("name" , itemDto.getName());
        query.setParameter("capacity" , itemDto.getCapacity());
        query.setParameter("description" , itemDto.getDescription());
        query.setParameter("price" , itemDto.getPrice());
        query.setParameter("discount" , itemDto.getDiscount());
        query.setParameter("itemCategory" , itemDto.getItemCategory());
        query.setParameter("itemSubCategory" , itemDto.getItemSubCategory());
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
    public int updateItemImages(String slug , String filename){
        String hql = "update Item set avtars =:avtars where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("avtars", filename);
        query.setParameter("slug", slug);
        return query.executeUpdate();
    }


    /// For updateItemsViaExcelSheet

    @Getter
    @Setter
    @ToString
    public static class ItemUpdateError {
        Map<String,Object> itemRowDetail;
        String errorMessage;
    }


    public int updateExcelSheetItems(ItemDto itemDto,Integer userId,Integer wholesaleId){
        String hql = """
           update Item set name=:name,
                label=:label,
                capacity=:capacity,
                price=:price,
                discount=:discount,
                inStock=:inStock,
                updatedAt=:updatedAt,
                updatedBy=:updatedBy
           where slug=:slug and wholesaleId=:wholesaleId
        """;
        Query query = entityManager.createQuery(hql);
        query.setParameter("name", itemDto.getName())
                .setParameter("label", itemDto.getLabel())
                .setParameter("capacity", itemDto.getCapacity())
                .setParameter("price", itemDto.getPrice())
                .setParameter("discount", itemDto.getDiscount())
                .setParameter("inStock", itemDto.getInStock())
                .setParameter("updatedAt", Utils.getCurrentMillis())
                .setParameter("updatedBy", userId)
                .setParameter("slug", itemDto.getSlug())
                .setParameter("wholesaleId",wholesaleId);
        return query.executeUpdate();
    }



}
