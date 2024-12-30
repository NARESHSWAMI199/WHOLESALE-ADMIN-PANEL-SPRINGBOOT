package com.sales.wholesaler.repository;


import com.sales.entities.UserPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleUserPlansRepository extends JpaRepository<UserPlans,Integer>, JpaSpecificationExecutor<UserPlans> {


   UserPlans findByPlanId(Integer planId);
   UserPlans findByUserId(Integer userId);
}
