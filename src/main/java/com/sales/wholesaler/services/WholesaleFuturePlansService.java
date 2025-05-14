package com.sales.wholesaler.services;


import com.sales.dto.SearchFilters;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.WholesalerFuturePlan;
import com.sales.exceptions.NotFoundException;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WholesaleFuturePlansService extends WholesaleRepoContainer {

    @Autowired
    private WholesaleServicePlanService  wholesaleServicePlanService;;

    public Page<WholesalerFuturePlan> getWholesalerFuturePlans(User loggedUser, SearchFilters filters) {
        Pageable pageable = getPageable(filters);
       return wholesaleFuturePlansRepository.findWholesalerFuturePlansByUserId(pageable,loggedUser.getId());
    }



    public int activateWholesalerFuturePlans(User loggedUser,String servicePlanSlug){
        ServicePlan servicePlan = wholesaleServicePlanRepository.findBySlug(servicePlanSlug);
        if(servicePlan == null) throw new NotFoundException("There is service plans. Check your parameters.");
        List<WholesalerFuturePlan> futurePlans = wholesaleFuturePlansRepository.findWholesalerFuturePlansByServicePlan(servicePlan);
        List<WholesalerFuturePlan> plans = futurePlans.stream().filter(futurePlan -> futurePlan.getServicePlan() != null && futurePlan.getServicePlan().getSlug().equals(servicePlanSlug)).toList();
        if(!plans.isEmpty()) {
            WholesalerFuturePlan futurePlan = plans.get(0);
            wholesaleServicePlanService.assignUserPlan(loggedUser.getId(), futurePlan.getServicePlan().getId());
            return wholesaleFuturePlansRepository.updateWholesalerFuturePlans(plans.get(0).getId(), Utils.getCurrentMillis());
        } else {
            throw new NotFoundException("There is no plans to activate.");
        }
    }


}
