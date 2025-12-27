package com.sales.wholesaler.services;


import com.sales.dto.SearchFilters;
import com.sales.entities.User;
import com.sales.entities.WholesalerFuturePlan;
import com.sales.exceptions.NotFoundException;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WholesaleFuturePlansService extends WholesaleRepoContainer {

    @Autowired
    private WholesaleServicePlanService  wholesaleServicePlanService;

    public Page<WholesalerFuturePlan> getWholesalerFuturePlans(User loggedUser, SearchFilters filters) {
        Pageable pageable = getPageable(filters);
       return wholesaleFuturePlansRepository.findWholesalerFuturePlansByUserIdAndStatus(pageable,loggedUser.getId(),"N"); // Getting only new not old or used.
    }



    public int activateWholesalerFuturePlans(User loggedUser,String futurePlanSlug){
        Map<String,Object> futurePlan = wholesaleFuturePlansRepository.getNewFuturePlanByUserIdAndSlug(futurePlanSlug,loggedUser.getId());
        Object servicePlanId = futurePlan.get("servicePlanId");
        Object wholesalerFuturePlanId = futurePlan.get("wholesalerFuturePlanId");
        if (servicePlanId == null || wholesalerFuturePlanId == null) throw  new NotFoundException("Not a valid request. Future plan not found.");
        wholesaleServicePlanService.assignUserPlan(loggedUser.getId(), (Integer) servicePlanId);
        return wholesaleFuturePlansRepository.updateWholesalerFuturePlans((Long) wholesalerFuturePlanId, Utils.getCurrentMillis());
    }


}
