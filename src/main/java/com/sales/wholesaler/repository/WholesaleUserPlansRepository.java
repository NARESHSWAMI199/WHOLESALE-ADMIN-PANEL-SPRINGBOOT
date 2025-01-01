package com.sales.wholesaler.repository;


import com.sales.entities.UserPlans;
import com.sales.utils.Utils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WholesaleUserPlansRepository extends JpaRepository<UserPlans,Integer>, JpaSpecificationExecutor<UserPlans> {


   UserPlans findByPlanId(Integer planId);
   UserPlans findByUserId(Integer userId);
   @Query(value = "select " +
           "sp.name as name, " +
           "sp.price as price, " +
           "sp.discount as discount, " +
           "sp.months as months, " +
           "up.id as userPlanId, " +
           "up.createdAt as createdAt, " +
           "up.expiryDate as expiryDate " +
           "from ServicePlan sp INNER JOIN UserPlans up ON up.planId = sp.id where up.userId = :userId")
   List<Map<String,Object>> getAllUserPlansByUserId(Integer userId);


}
