package com.sales.admin.services;


import com.sales.dto.DeleteDto;
import com.sales.dto.ServicePlanDto;
import com.sales.dto.StatusDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.specifications.PlansSpecifications;
import com.sales.specifications.ServicePlanSpecification;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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



    @Transactional
    public ServicePlan insertServicePlan(User loggedUser, ServicePlanDto servicePlanDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("You don't have permission to perform this action. Contact to your administrator.",null);

        // Validating required fields if there we found any required field is null, then it will throw an Exception
        Utils.checkRequiredFields(servicePlanDto, List.of("planName","price","discount","months","description"));

        if(servicePlanDto.getPrice() < 0) throw new IllegalArgumentException("Price can't be less than 0.");
        if(servicePlanDto.getDiscount() < 0 || servicePlanDto.getDiscount() > servicePlanDto.getPrice()) throw new IllegalArgumentException("Discount can't be greater than price and can't be less than 0.");

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

    public Map<String,Object> updateServicePlanStatus(StatusDto statusDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // Validating required fields if there we found any required field is null, then it will throw an Exception
        Utils.checkRequiredFields(statusDto, List.of("status","slug"));

        String status = statusDto.getStatus();
        Map<String, Object> result = new HashMap<>();
        if (!loggedUser.getUserType().equals("SA"))
            throw new PermissionDeniedDataAccessException("You don't have permission to perform this action.Contact to your administrator",null);

        switch (status) {
            case "A", "D":
                int isUpdated = servicePlanHbRepository.updateServicePlansStatus(status, statusDto.getSlug(), loggedUser);
                if (isUpdated > 0) {
                    if (status.equals("A")) {
                        result.put("message", "Successfully Activated.");
                    } else {
                        result.put("message", "Successfully Deactivated.");
                    }
                    result.put("status", 201);
                } else {
                    result.put("message", "No plan found to update.");
                    result.put("status", 404);
                }
                return result;
            default:
                throw new IllegalArgumentException("status must be A or D.");
        }
    }

    public Map<String,Object> deletedServicePlan(DeleteDto deleteDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        // Validating required fields if there we found any required field is null, then it will throw an Exception
        Utils.checkRequiredFields(deleteDto, List.of("slug"));
        String slug = deleteDto.getSlug();
        Map<String,Object> result = new HashMap<>();
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("You don't have permission to perform this action.Contact to your administrator.",null);
        int isUpdated = servicePlanHbRepository.deleteServicePlan(slug, loggedUser);
        if(isUpdated > 0){
            result.put("message","Service plan successfully deleted.");
            result.put("status", 201);
        }else{
            result.put("message","No service plan found to delete.");
            result.put("status",404);
        }
        return result;
    }


}
