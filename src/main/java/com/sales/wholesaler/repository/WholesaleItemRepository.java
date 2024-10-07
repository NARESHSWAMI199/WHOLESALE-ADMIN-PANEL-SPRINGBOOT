package com.sales.wholesaler.repository;


import com.sales.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleItemRepository extends JpaRepository<Item, Integer> , JpaSpecificationExecutor {

   Item findItemBySlug(String slug);

   @Query(value = "select count(id) as count from Item")
   Integer totalItemCount();

   @Query(value = "select count(id) as count from Item where status=:status")
   Integer optionItemCount(@Param("status") String status);

   @Query(value = "select count(id) as count from Item where label=:label")
   Integer getItemCountLabel(@Param("label") String label);

   @Query(value = "select count(id) as count from Item where label=:label and status=:status")
   Integer optionItemCountLabel(@Param("label") String label,@Param("status") String status);

   @Query(value = "select count(id) as count from Item where inStock=:inStock")
   Integer getItemCountInStock(@Param("inStock") String inStock);

   @Query(value = "select count(id) as count from Item where inStock=:inStock and status=:status")
   Integer optionItemCountInStock(@Param("inStock") String inStock,@Param("status") String status);

}
