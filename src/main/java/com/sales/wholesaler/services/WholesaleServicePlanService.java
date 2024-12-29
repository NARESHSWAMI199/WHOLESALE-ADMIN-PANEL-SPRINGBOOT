package com.sales.wholesaler.services;


import com.sales.admin.services.RepoContainer;
import com.sales.entities.ServicePlan;
import com.sales.utils.Utils;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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
        ServicePlan plan = wholesaleServicePlanRepository.findById(planId).get();
        Long createdAt = plan.getCreatedAt();
        Integer months = plan.getMonths();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt);
        calendar.add(Calendar.MONTH, months);
        long expiryDate = calendar.getTimeInMillis();
        long currentDate =  Utils.getCurrentMillis();
        return currentDate <= expiryDate;
    }

}
