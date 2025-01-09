package com.sales.wholesaler.services;


import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sales.specifications.PlansSpecifications.*;

@Service
public class WholesaleServicePlanService extends WholesaleRepoContainer {

    public List<ServicePlan> getALlServicePlan(){
        return wholesaleServicePlanRepository.findAll().stream().filter(servicePlan -> servicePlan.getPrice() > 0).toList();

    }

    public ServicePlan findBySlug(String slug){
        return wholesaleServicePlanRepository.findBySlug(slug);
    }

    public boolean isPlanActive(Integer  userPlanId){
        if(userPlanId == null) return false;
        Optional<UserPlans> plan = wholesaleUserPlansRepository.findById(userPlanId);
        if (plan.isPresent()){
            UserPlans userPlan = plan.get();
            long expiryDate = userPlan.getExpiryDate();
            long currentDate =  Utils.getCurrentMillis();
            return currentDate <= expiryDate;
        }
        return false;
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
                .slug(UUID.randomUUID().toString())
                .userId(userId)
                .servicePlan(plan)
                .createdAt(currentMillis)
                .expiryDate(expiryDate)
                .createdBy(userId)
                .build();
        UserPlans userPlan = wholesaleUserPlansRepository.save(userPlans);
        wholesaleUserHbRepository.updateUserActivePlan(userId,userPlan.getId());

    }

/*    public List<Map<String,Object>> getAllUserPlans(User loggedUser){
        List<Map<String, Object>> allUserPlansByUserId = wholesaleUserPlansRepository.getAllUserPlansByUserId(loggedUser.getId());
        List<Map<String,Object>> result  = new ArrayList<>();
        for (Map<String, Object> plan : allUserPlansByUserId) {*//* we can direct update in plan, but we're facing Exception : A TupleBackedMap cannot be modified; *//*
            Map<String, Object> updatePlan = new HashMap<>(plan);
            updatePlan.put("status", isPlanActive((Integer) plan.get("userPlanId")));
            result.add(updatePlan);
        }
        return result;
    }*/


    public Page<UserPlans> getAllUserPlans(User loggedUser , UserPlanDto searchFilters){
        Specification<UserPlans> specification = Specification.where(
                hasSlug(searchFilters.getSlug())
                .and(greaterThanOrEqualCreatedFromDate(searchFilters.getCreatedFromDate()))
                .and(lessThanOrEqualToCreatedToDate(searchFilters.getCreatedToDate()))
                .and(greaterThanOrEqualExpiredFromDate(searchFilters.getExpiredFromDate()))
                .and(lessThanOrEqualToExpiredToDate(searchFilters.getExpiredToDate()))
                .and(isStatus(searchFilters.getStatus()))
                .and(isUserId(loggedUser.getId()))
        );
        Pageable pageable = getPageable(searchFilters);
        return  wholesaleUserPlansRepository.findAll(specification, pageable);
    }


    public ServicePlan getDefaultServicePlan() {
        return wholesaleServicePlanRepository.getDefaultServicePlan();
    }

}
