package com.sales.wholesaler.repository;


import com.sales.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WholesaleItemRepository extends JpaRepository<Item, Integer> , JpaSpecificationExecutor<Item> {



   @Query("from Item where wholesaleId=:wholesaleId and createdAt >= :fromDate and createdAt <= :toDate")
   List<Item> getAllItemsWithFilters(@Param("wholesaleId")Integer wholesaleId,
                                     @Param("fromDate") Long fromDate,
                                     @Param("toDate") Long toDate);


   Item findItemBySlug(String slug);


   @Query(value = "select status from Item where slug=:slug")
   String getItemStatus(String slug);

   @Query(value = "select count(id) as count from Item where wholesaleId=:id")
   Integer totalItemCount(@Param("id") Integer storeId);

   @Query(value = "select count(id) as count from Item where wholesaleId=:id and status=:status")
   Integer optionItemCount(@Param("status") String status,@Param("id") Integer storeId);

   @Query(value = "select count(id) as count from Item where wholesaleId=:id and label=:label")
   Integer getItemCountLabel(@Param("label") String label,@Param("id") Integer storeId);

   @Query(value = "select count(id) as count from Item where wholesaleId=:id and label=:label and status=:status")
   Integer optionItemCountLabel(@Param("label") String label,@Param("status") String status,@Param("id") Integer storeId);

   @Query(value = "select count(id) as count from Item where wholesaleId=:id and inStock=:inStock")
   Integer getItemCountInStock(@Param("inStock") String inStock,@Param("id") Integer storeId);


   @Query(value = "select count(id) as count from Item where wholesaleId=:id  and label=:label and inStock=:inStock")
   Integer getItemCountInStock(@Param("inStock") String inStock,@Param("label") String label,@Param("id") Integer storeId);


   @Query(value = "select count(id) as count from Item where wholesaleId=:id and inStock=:inStock and status=:status")
   Integer optionItemCountInStock(@Param("inStock") String inStock,@Param("status") String status,@Param("id") Integer storeId);


//   @Query(value = "SELECT count(id) from item s where wholesale_id=:storeId and  FROM_UNIXTIME(created_at /1000,'%m') =:month and FROM_UNIXTIME(created_at /1000,'%Y') =:year and is_deleted='N'",nativeQuery = true)
//   Integer totalItemsViaMonth(@Param("month") Integer month, @Param("year") Integer year, @Param("storeId")Integer storeId);

   // Improved
   @Query(value = """
    SELECT count(id) 
    FROM item s 
    WHERE wholesale_id = :storeId
      AND is_deleted = 'N'
      AND created_at >= :startOfMonth
      AND created_at < :startOfNextMonth
   """, nativeQuery = true)
   Integer totalItemsViaMonth(
           @Param("storeId") Integer storeId,
           @Param("startOfMonth") Long startOfMonth,
           @Param("startOfNextMonth") Long startOfNextMonth
   );


}
