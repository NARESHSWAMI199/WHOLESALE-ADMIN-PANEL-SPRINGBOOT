package com.sales.wholesaler.repository;

import com.sales.entities.ServicePlan;
import com.sales.entities.WholesalerFuturePlan;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WholesaleFuturePlansRepository extends JpaRepository<WholesalerFuturePlan, Long>, JpaSpecificationExecutor<WholesalerFuturePlan> {

    Page<WholesalerFuturePlan> findWholesalerFuturePlansByUserIdAndStatus(Pageable pageable, Integer userId,String status);

    List<WholesalerFuturePlan> findWholesalerFuturePlansByServicePlanAndUserIdAndStatus(ServicePlan servicePlan,Integer userId,String status);


    @Modifying
    @Transactional
    @Query(value = "UPDATE WholesalerFuturePlan set status = 'O', updatedAt =:updatedAt where id = :id ")
    int updateWholesalerFuturePlans(@Param("id") Long id,Long updatedAt);

}
