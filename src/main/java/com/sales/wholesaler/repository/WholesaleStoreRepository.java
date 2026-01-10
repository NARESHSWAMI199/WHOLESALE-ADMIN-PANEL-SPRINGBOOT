package com.sales.wholesaler.repository;

import com.sales.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleStoreRepository extends JpaRepository<Store, Integer> {

    Store findStoreBySlug(String slug);

    Store findStoreByUserId(int userId);

    @Query(value = "select id as id from Store s where s.user.id=:userId")
    Integer getStoreIdByUserId(@Param("userId")Integer userId);

    @Query("SELECT a.id FROM Store s JOIN s.address a WHERE s.slug = :slug")
    Integer getAddressIdBySlug(String slug);

}
