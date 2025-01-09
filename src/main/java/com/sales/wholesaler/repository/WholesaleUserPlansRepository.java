package com.sales.wholesaler.repository;


import com.sales.entities.UserPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleUserPlansRepository extends JpaRepository<UserPlans,Integer>, JpaSpecificationExecutor<UserPlans> {

   UserPlans findByUserId(Integer userId);
   /*
   @Query(value = "select " +
           "sp.name as name, " +
           "sp.price as price, " +
           "sp.discount as discount, " +
           "sp.months as months, " +
           "up.id as userPlanId, " +
           "up.createdAt as createdAt, " +
           "up.expiryDate as expiryDate " +
           "from ServicePlan sp INNER JOIN UserPlans up ON up.planId = sp.id where up.userId = :userId order by up.id desc")
   List<Map<String,Object>> getAllUserPlansByUserId(Integer userId);
    */


}
