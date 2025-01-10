package com.sales.admin.services;


import com.sales.dto.ServicePlanDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.exceptions.MyException;
import com.sales.specifications.PlansSpecifications;
import com.sales.specifications.ServicePlanSpecification;
import com.sales.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServicePlanService extends  RepoContainer {

    public Page<ServicePlan> getALlServicePlan(ServicePlanDto servicePlanDto){
        Specification specification = Specification.where(
                ServicePlanSpecification.containsName(servicePlanDto.getName())
                        .and(ServicePlanSpecification.hasSlug(servicePlanDto.getSlug()))
                        .and(ServicePlanSpecification.isStatus(servicePlanDto.getStatus()))
                        .and(ServicePlanSpecification.greaterThanOrEqualFromDate(servicePlanDto.getFromDate()))
                        .and(ServicePlanSpecification.lessThanOrEqualToToDate(servicePlanDto.getToDate()))
        );
        Pageable pageable = getPageable(servicePlanDto);
        return servicePlanRepository.findAll(specification,pageable);
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
                PlansSpecifications.hasSlug(searchFilters.getSlug())
                .and(PlansSpecifications.greaterThanOrEqualCreatedFromDate(searchFilters.getCreatedFromDate()))
                .and(PlansSpecifications.lessThanOrEqualToCreatedToDate(searchFilters.getCreatedToDate()))
                .and(PlansSpecifications.greaterThanOrEqualExpiredFromDate(searchFilters.getExpiredFromDate()))
                .and(PlansSpecifications.lessThanOrEqualToExpiredToDate(searchFilters.getExpiredToDate()))
                .and(PlansSpecifications.isStatus(searchFilters.getStatus()))
                .and(PlansSpecifications.isUserId(userId))
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

    public Map<String,Object> updateServicePlanStatus(String status,String slug, User loggedUser){
        Map<String,Object> result = new HashMap<>();
        if(!loggedUser.getUserType().equals("SA")) throw new MyException("You don't have permission to perform this action.");
        int isUpdated = servicePlanHbRepository.updateServicePlansStatus(status, slug, loggedUser);
        if(isUpdated > 0){
            if(status.equals("A")){
                result.put("message","Successfully Activated.");
            }else{
                result.put("message","Successfully Deactivated.");
            }
            result.put("status", 201);
        }else{
            result.put("message","Something went wrong.");
            result.put("status",400);
        }
        return result;
    }

    public Map<String,Object> deletedServicePlan(String slug, User loggedUser){
        Map<String,Object> result = new HashMap<>();
        if(!loggedUser.getUserType().equals("SA")) throw new MyException("You don't have permission to perform this action.");
        int isUpdated = servicePlanHbRepository.deleteServicePlan(slug, loggedUser);
        if(isUpdated > 0){
            result.put("message","Service plan successfully deleted.");
            result.put("status", 201);
        }else{
            result.put("message","Something went wrong.");
            result.put("status",400);
        }
        return result;
    }


}
