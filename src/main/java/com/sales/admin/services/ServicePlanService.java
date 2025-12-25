package com.sales.admin.services;


import com.sales.dto.DeleteDto;
import com.sales.dto.ServicePlanDto;
import com.sales.dto.StatusDto;
import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.WholesalerPlans;
import com.sales.global.ConstantResponseKeys;
import com.sales.specifications.PlansSpecifications;
import com.sales.specifications.ServicePlanSpecification;
import com.sales.utils.Utils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class ServicePlanService extends  RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(ServicePlanService.class);

    public Page<ServicePlan> getALlServicePlan(ServicePlanDto servicePlanDto){
        logger.info("Entering getALlServicePlan with servicePlanDto: {}", servicePlanDto);
        Specification<ServicePlan> specification = Specification.allOf(
                ServicePlanSpecification.containsName(servicePlanDto.getName())
                        .and(ServicePlanSpecification.hasSlug(servicePlanDto.getSlug()))
                        .and(ServicePlanSpecification.isStatus(servicePlanDto.getStatus()))
                        .and(ServicePlanSpecification.greaterThanOrEqualFromDate(servicePlanDto.getFromDate()))
                        .and(ServicePlanSpecification.lessThanOrEqualToToDate(servicePlanDto.getToDate()))
        );
        Pageable pageable = getPageable(servicePlanDto);
        Page<ServicePlan> result = servicePlanRepository.findAll(specification,pageable);
        logger.info("Exiting getALlServicePlan");
        return result;
    }

    public ServicePlan findBySlug(String slug){
        logger.info("Entering findBySlug with slug: {}", slug);
        ServicePlan result = servicePlanRepository.findBySlug(slug);
        logger.info("Exiting findBySlug");
        return result;
    }

    public boolean isPlanActive(Integer  userPlanId){
        logger.info("Entering isPlanActive with userPlanId: {}", userPlanId);
        if(userPlanId == null) return false;
        Optional<WholesalerPlans> plan = wholesalerPlansRepository.findById(userPlanId);
        if (plan.isPresent()){
            WholesalerPlans userPlan = plan.get();
            long expiryDate = userPlan.getExpiryDate();
            long currentDate =  Utils.getCurrentMillis();
            boolean isActive = currentDate <= expiryDate;
            logger.info("Exiting isPlanActive with result: {}", isActive);
            return isActive;
        }
        logger.info("Exiting isPlanActive with result: false");
        return false;
    }

    public Page<WholesalerPlans> getAllUserPlans(Integer userId , UserPlanDto searchFilters){
        logger.info("Entering getAllUserPlans with userId: {}, searchFilters: {}", userId, searchFilters);
        Specification<WholesalerPlans> specification = Specification.allOf(
                PlansSpecifications.hasSlug(searchFilters.getSlug())
                .and(PlansSpecifications.greaterThanOrEqualCreatedFromDate(searchFilters.getCreatedFromDate()))
                .and(PlansSpecifications.lessThanOrEqualToCreatedToDate(searchFilters.getCreatedToDate()))
                .and(PlansSpecifications.greaterThanOrEqualExpiredFromDate(searchFilters.getExpiredFromDate()))
                .and(PlansSpecifications.lessThanOrEqualToExpiredToDate(searchFilters.getExpiredToDate()))
                .and(PlansSpecifications.isStatus(searchFilters.getStatus()))
                .and(PlansSpecifications.isUserId(userId))
        );
        Pageable pageable = getPageable(searchFilters);
        Page<WholesalerPlans> result = wholesalerPlansRepository.findAll(specification, pageable);
        logger.info("Exiting getAllUserPlans");
        return result;
    }



    @Transactional
    public ServicePlan insertServicePlan(User loggedUser, ServicePlanDto servicePlanDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering insertServicePlan with loggedUser: {}, servicePlanDto: {}", loggedUser, servicePlanDto);
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("You don't have permission to perform this action. Contact to your administrator.",new Exception());

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
        ServicePlan result = servicePlanRepository.save(servicePlan);
        logger.info("Exiting insertServicePlan");
        return result;
    }

    public Map<String,Object> updateServicePlanStatus(StatusDto statusDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering updateServicePlanStatus with statusDto: {}, loggedUser: {}", statusDto, loggedUser);
        // Validating required fields if there we found any required field is null, then it will throw an Exception
        Utils.checkRequiredFields(statusDto, List.of("status","slug"));

        String status = statusDto.getStatus();
        Map<String, Object> result = new HashMap<>();
        if (!loggedUser.getUserType().equals("SA"))
            throw new PermissionDeniedDataAccessException("You don't have permission to perform this action.Contact to your administrator",new Exception());

        switch (status) {
            case "A", "D":
                int isUpdated = servicePlanHbRepository.updateServicePlansStatus(status, statusDto.getSlug(), loggedUser);
                if (isUpdated > 0) {
                    if (status.equals("A")) {
                        result.put(ConstantResponseKeys.MESSAGE, "Successfully Activated.");
                    } else {
                        result.put(ConstantResponseKeys.MESSAGE, "Successfully Deactivated.");
                    }
                    result.put(ConstantResponseKeys.STATUS, 200);
                } else {
                    result.put(ConstantResponseKeys.MESSAGE, "No plan found to update.");
                    result.put(ConstantResponseKeys.STATUS, 404);
                }
                logger.info("Exiting updateServicePlanStatus with result: {}", result);
                return result;
            default:
                throw new IllegalArgumentException("status must be A or D.");
        }
    }

    public Map<String,Object> deletedServicePlan(DeleteDto deleteDto, User loggedUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        logger.info("Entering deletedServicePlan with deleteDto: {}, loggedUser: {}", deleteDto, loggedUser);
        // Validating required fields if their we found any required field is null, then it will throw an Exception
        Utils.checkRequiredFields(deleteDto, List.of("slug"));
        String slug = deleteDto.getSlug();
        Map<String,Object> result = new HashMap<>();
        if(!loggedUser.getUserType().equals("SA")) throw new PermissionDeniedDataAccessException("You don't have permission to perform this action.Contact to your administrator.",new Exception());
        int isUpdated = servicePlanHbRepository.deleteServicePlan(slug, loggedUser);
        if(isUpdated > 0){
            result.put(ConstantResponseKeys.MESSAGE,"Service plan successfully deleted.");
            result.put(ConstantResponseKeys.STATUS, 200);
        }else{
            result.put(ConstantResponseKeys.MESSAGE,"No service plan found to delete.");
            result.put(ConstantResponseKeys.STATUS,404);
        }
        logger.info("Exiting deletedServicePlan with result: {}", result);
        return result;
    }


}
