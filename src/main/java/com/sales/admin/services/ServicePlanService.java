package com.sales.admin.services;


import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.UserPlans;
import com.sales.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.sales.specifications.PlansSpecifications.*;

@Service
public class ServicePlanService extends  RepoContainer {

    public List<ServicePlan> getALlServicePlan(){
        return servicePlanRepository.findAll();
    }

    public ServicePlan findBySlug(String slug){
        return servicePlanRepository.findBySlug(slug);
    }

    public boolean isPlanActive(Integer  userPlanId){
        if(userPlanId == null) return false;
        Optional<UserPlans> plan = userPlansRepository.findById(userPlanId);
        if (plan.isPresent()){
            UserPlans userPlan = plan.get();
            long expiryDate = userPlan.getExpiryDate();
            long currentDate =  Utils.getCurrentMillis();
            return currentDate <= expiryDate;
        }
        return false;
    }

    public Page<UserPlans> getAllUserPlans(Integer userId , UserPlanDto searchFilters){
        Specification<UserPlans> specification = Specification.where(
                hasSlug(searchFilters.getSlug())
                .and(greaterThanOrEqualCreatedFromDate(searchFilters.getCreatedFromDate()))
                .and(lessThanOrEqualToCreatedToDate(searchFilters.getCreatedToDate()))
                .and(greaterThanOrEqualExpiredFromDate(searchFilters.getExpiredFromDate()))
                .and(lessThanOrEqualToExpiredToDate(searchFilters.getExpiredToDate()))
                .and(isStatus(searchFilters.getStatus()))
                .and(isUserId(userId))
        );
        Pageable pageable = getPageable(searchFilters);
        Page<UserPlans> allUserPlans = userPlansRepository.findAll(specification, pageable);
        return allUserPlans;
    }

}
