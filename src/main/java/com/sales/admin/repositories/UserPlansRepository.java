package com.sales.admin.repositories;


import com.sales.entities.UserPlans;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserPlansRepository extends JpaRepository<UserPlans,Integer> , JpaSpecificationExecutor<UserPlans> {

    UserPlans findByPlanId(Integer planId);
    UserPlans findByUserId(Integer userId);
    @Query(value = "select " +
            "sp.name as name, " +
            "sp.price as price, " +
            "sp.discount as discount, " +
            "sp.months as months, " +
            "up.id as userPlanId, " +
            "up.slug as slug, "+
            "up.createdAt as createdAt, " +
            "up.expiryDate as expiryDate " +
            "from ServicePlan sp INNER JOIN UserPlans up ON up.planId = sp.id where up.userId = :userId order by up.id desc")
    List<Map<String,Object>> getAllUserPlansByUserId(Integer userId, Specification<UserPlans> specification , Pageable pageable);


}
