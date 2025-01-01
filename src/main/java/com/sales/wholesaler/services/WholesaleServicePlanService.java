package com.sales.wholesaler.services;


import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WholesaleServicePlanService extends WholesaleRepoContainer {

    public List<ServicePlan> getALlServicePlan(){
        return wholesaleServicePlanRepository.findAll();
    }

    public ServicePlan findBySlug(String slug){
        return wholesaleServicePlanRepository.findBySlug(slug);
    }

    public boolean isPlanActive(Integer  userPlanId){
        if(userPlanId == null) return false;
        UserPlans plan = wholesaleUserPlansRepository.findByPlanId(userPlanId);
        long expiryDate = plan.getExpiryDate();
        long currentDate =  Utils.getCurrentMillis();
        return currentDate <= expiryDate;
    }


    public void assignUserPlan(int userId , int servicePlanId ){
        Long currentMillis = Utils.getCurrentMillis();
        ServicePlan plan = wholesaleServicePlanRepository.findById(servicePlanId).get();
        Integer months = plan.getMonths();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMillis);
        calendar.add(Calendar.MONTH, months);
        long expiryDate = calendar.getTimeInMillis();
        UserPlans userPlans = UserPlans.builder()
                .userId(userId)
                .planId(servicePlanId)
                .createdAt(currentMillis)
                .expiryDate(expiryDate)
                .createdBy(userId)
                .build();
        UserPlans userPlan = wholesaleUserPlansRepository.save(userPlans);
        wholesaleUserHbRepository.updateUserActivePlan(userPlan.getId(),userId);

    }

    public List<Map<String,Object>> getAllUserPlans(User loggedUser){
        List<Map<String, Object>> allUserPlansByUserId = wholesaleUserPlansRepository.getAllUserPlansByUserId(loggedUser.getId());
        allUserPlansByUserId.forEach(plan -> {
            /* we can direct update in plan, but we're facing Exception : A TupleBackedMap cannot be modified; */
            Map<String,Object> updatePlan = new HashMap<>(plan);
            updatePlan.put("status",isPlanActive((Integer) plan.get("userPlanId")));
        });
        return allUserPlansByUserId;
    }

}
