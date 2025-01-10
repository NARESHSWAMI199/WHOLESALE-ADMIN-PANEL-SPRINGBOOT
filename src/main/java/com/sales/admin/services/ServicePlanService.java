package com.sales.admin.services;


import com.sales.dto.ServicePlanDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.sales.specifications.PlansSpecifications.*;

@Service
public class ServicePlanService extends  RepoContainer {

    public Page<ServicePlan> getALlServicePlan(ServicePlanDto servicePlanDto){
        Pageable pageable = getPageable(servicePlanDto);
        return servicePlanRepository.findAll(pageable);
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


    public ServicePlan insertServicePlan(User loggedUser, ServicePlanDto servicePlanDto){

        if(servicePlanDto.getPrice() < 0) throw new MyException("Price can't be less than 0.");
        if(servicePlanDto.getDiscount() < 0 || servicePlanDto.getDiscount() > servicePlanDto.getPrice()) throw new MyException("Discount can't be greater than price and can't be less than 0.");

        ServicePlan servicePlan = ServicePlan.builder()
                .name(servicePlanDto.getPlanName())
                .price(servicePlanDto.getPrice())
                .discount(servicePlanDto.getDiscount())
                .months(servicePlanDto.getMonths())
                .description(servicePlanDto.getDescription())
                .createdBy(loggedUser.getId())
                .updatedBy(loggedUser.getId())
                .slug(UUID.randomUUID().toString())
                .status("A")
                .createdAt(Utils.getCurrentMillis())
                .updatedAt(Utils.getCurrentMillis())
                .isDeleted("N")
                .build();
        return servicePlanRepository.save(servicePlan);
    }

}
