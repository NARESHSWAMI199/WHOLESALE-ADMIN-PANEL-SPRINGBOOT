package com.sales.wholesaler.services;


import com.sales.entities.ServicePlan;
import com.sales.entities.UserPlans;
import com.sales.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class WholesaleServicePlanService extends WholesaleRepoContainer {

    public List<ServicePlan> getALlServicePlan(){
        return wholesaleServicePlanRepository.findAll();
    }

    public ServicePlan findBySlug(String slug){
        return wholesaleServicePlanRepository.findBySlug(slug);
    }

    public boolean isPlanActive(Integer  planId){
        if(planId == null) return false;
        UserPlans plan = wholesaleUserPlansRepository.findByPlanId(planId);
        long expiryDate = plan.getExpiryDate();
        long currentDate =  Utils.getCurrentMillis();
        return currentDate <= expiryDate;
    }


    public void assignUserPlan(int userId , int planId ){

        Long currentMillis = Utils.getCurrentMillis();

        ServicePlan plan = wholesaleServicePlanRepository.findById(planId).get();
        Integer months = plan.getMonths();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMillis);
        calendar.add(Calendar.MONTH, months);
        long expiryDate = calendar.getTimeInMillis();

        UserPlans userPlans = UserPlans.builder()
                .userId(userId)
                .planId(planId)
                .createdAt(currentMillis)
                .expiryDate(expiryDate)
                .createdBy(userId)
                .build();
        wholesaleUserPlansRepository.save(userPlans);
    }

}
