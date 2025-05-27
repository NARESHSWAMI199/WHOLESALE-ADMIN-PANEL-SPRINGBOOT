package com.sales.admin.repositories;

import com.sales.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> , JpaSpecificationExecutor<Store> {

    Store findStoreBySlug(String slug);

    Store findStoreByUserId(int userId);

    @Query(value = "select count(id) as count from Store")
    Integer totalWholesaleCount();
    @Query(value = "select count(id) as count from Store where status=:status")
    Integer optionWholesaleCount(@Param("status") String status);
    @Query(value = "SELECT count(id) from store s where FROM_UNIXTIME(created_at /1000,'%m') =:month and FROM_UNIXTIME(created_at /1000,'%Y') =:year and is_deleted='N'",nativeQuery = true)
    Integer totalStoreViaMonth(@Param("month") Integer month,@Param("year") Integer year);

    @Query("SELECT a.id FROM Store s JOIN s.address a WHERE s.slug = :slug")
    Integer getAddressIdBySlug(String slug);

    @Query(value = "select id from Store where slug=:slug")
    Integer getStoreIdByStoreSlug(String slug);


    @Query(value = "select id from store where user_id=:userId",nativeQuery = true)
    Integer getStoreIdByUserId(@Param("userId")Integer userId);

}
