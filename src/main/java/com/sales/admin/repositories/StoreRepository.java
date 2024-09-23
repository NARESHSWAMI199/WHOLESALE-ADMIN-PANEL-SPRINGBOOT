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
}
