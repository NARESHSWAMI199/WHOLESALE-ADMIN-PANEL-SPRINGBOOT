package com.sales.admin.repositories;


import com.sales.dto.ItemDto;
import com.sales.entities.User;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Transactional
public class ItemHbRepository{

    @Autowired
    EntityManager entityManager;

    public int updateItems(ItemDto itemDto, User loggedUser){
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
                "where slug =:slug ";
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
        return  query.executeUpdate();
    }


    public int deleteItem(String slug){
        String hqlString = "update Item set isDeleted='Y' where slug=:slug";
        Query query = entityManager.createQuery(hqlString);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int updateStock(String stock , String slug){
        String hqlString = "update Item set inStock=:stock where slug=:slug";
        Query query = entityManager.createQuery(hqlString);
        query.setParameter("stock",stock);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int updateStatus(String slug, String status){
        String hql = "Update Item set status=:status where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("status",status);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int insertItemsList(Map itemsData,int userId,int wholesaleId){
        List nameList = (List) itemsData.get("NAME");
        List labelList = (List) itemsData.get("LABEL");
        List priceList = (List) itemsData.get("PRICE");
        List discountlist = (List) itemsData.get("DISCOUNT");
        List descriptionList = (List) itemsData.get("DESCRIPTION");
        List avatarList = (List) itemsData.get("AVATAR");
        List in_stockList = (List) itemsData.get("STOCK");

        String dataList = "";
        for (int i = 0; i < nameList.size(); i++){
            String label = labelList.get(i).equals("") ? "N" : (String) labelList.get(i);
            String in_stock = in_stockList.get(i).equals("") ? "N" : (String) in_stockList.get(i);
            String discount = discountlist.get(i).equals("") ? "0" : (String) discountlist.get(i);
            String status = "A";
            int price = 0;
            if (priceList.get(i).equals("")){
                status = "D";
            }else {
                price = Integer.valueOf((String) priceList.get(i));
            }
            dataList += "(" +
                "'"+nameList.get(i) +"'," +
                    wholesaleId +","+
                "'"+label+"'," +
                    price +"," +
                    discount +"," +
                "'"+ descriptionList.get(i) +"'," +
                "'"+avatarList.get(i) +"'," +
                "'0'," +
                "'"+status+"'," +
                "'N'," +
                    Utils.getCurrentMillis() +"," +
                    userId +"," +
                    Utils.getCurrentMillis()+"," +
                    userId +"," +
                "'"+UUID.randomUUID()+"',"+
                "'"+in_stock+"'" +
                    ")";
            if (i != nameList.size()-1) dataList+=",";
        }
        System.out.println(dataList);

        String qs = "insert into item (" +
                "name," +
                "wholesale_id," +
                "label," +
                "price," +
                "discount," +
                "description," +
                "avatar," +
                "rating," +
                "status," +
                "is_deleted," +
                "created_at," +
                "created_by," +
                "updated_at," +
                "updated_by," +
                "slug," +
                "in_stock" +
                ") values " + dataList ;

        Query query = entityManager.createNativeQuery(qs);
        return  query.executeUpdate();
    }


    public int updateItemImage(String slug , String filenames){
        String hql = "update Item set avtars =:avtars where slug=:slug";
        Query query = entityManager.createQuery(hql);
        query.setParameter("avtars", filenames);
        query.setParameter("slug", slug);
        return query.executeUpdate();
    }


    public int deleteItemCategory(String slug){
        String hqlString = "update ItemCategory set isDeleted='Y' where slug=:slug";
        Query query = entityManager.createQuery(hqlString);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }


    public int deleteItemSubCategory(String slug){
        String hqlString = "update ItemSubCategory set isDeleted='Y' where slug=:slug";
        Query query = entityManager.createQuery(hqlString);
        query.setParameter("slug",slug);
        return query.executeUpdate();
    }

    public int getItemCategoryIdBySlug(String slug){
        String hql = "select id from ItemCategory where slug=:slug ";
        Query query = entityManager.createQuery(hql);
        query.setParameter("slug",slug);
        return (Integer) query.getSingleResult();
    }

    public int switchCategoryToOther(int categoryId){
        String sql = "Update item set category=0 , subcategory=0 where category=:categoryId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("categoryId",categoryId);
        return query.executeUpdate();
    }

    public int switchSubCategoryToOther(int subcategoryId){
        String sql = "Update item set category=0 , subcategory=0 where subcategory=:subcategoryId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("subcategoryId",subcategoryId);
        return query.executeUpdate();
    }



}
