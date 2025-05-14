package com.sales.wholesaler.services;


import com.sales.dto.UserPlanDto;
import com.sales.entities.ServicePlan;
import com.sales.entities.User;
import com.sales.entities.WholesalerFuturePlan;
import com.sales.entities.WholesalerPlans;
import com.sales.exceptions.NotFoundException;
import com.sales.utils.Utils;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(WholesaleServicePlanService.class);

    public List<ServicePlan> getALlServicePlan() {
        logger.info("Starting getALlServicePlan method");
        List<ServicePlan> servicePlans = wholesaleServicePlanRepository.findAll().stream().filter(servicePlan -> servicePlan.getPrice() > 0).toList();
        logger.info("Completed getALlServicePlan method");
        return servicePlans;
    }

    public ServicePlan findBySlug(String slug) {
        logger.info("Starting findBySlug method with slug: {}", slug);
        ServicePlan servicePlan = wholesaleServicePlanRepository.findBySlug(slug);
        logger.info("Completed findBySlug method");
        return servicePlan;
    }

    public boolean isPlanActive(Integer userPlanId) {
        logger.info("Starting isPlanActive method with userPlanId: {}", userPlanId);
        if (userPlanId == null) return false;
        Optional<WholesalerPlans> plan = wholesaleUserPlansRepository.findById(userPlanId);
        if (plan.isPresent()) {
            WholesalerPlans userPlan = plan.get();
            long expiryDate = userPlan.getExpiryDate();
            long currentDate = Utils.getCurrentMillis();
            boolean isActive = currentDate <= expiryDate;
            logger.info("Completed isPlanActive method");
            return isActive;
        }
        logger.info("Completed isPlanActive method");
        return false;
    }

    public void assignUserPlan(int userId, int servicePlanId) {
        logger.info("Starting assignUserPlan method with userId: {}, servicePlanId: {}", userId, servicePlanId);
        Long currentMillis = Utils.getCurrentMillis();
        // Checking user last plan expired or not.
        WholesalerPlans lastPlan = wholesaleUserPlansRepository.findLastPlanByUserId(userId,entityManager);
        ServicePlan plan = wholesaleServicePlanRepository.findById(servicePlanId).get();

        if(lastPlan !=null && lastPlan.getExpiryDate() > currentMillis){ // if last plans is not expired.
            logger.info("Going to adding this plan as future plan.");
            WholesalerFuturePlan wholesalerFuturePlan = WholesalerFuturePlan.builder()
                    .userId(userId)
                    .servicePlan(plan)
                    .status("N") // It's a new future plan.
                    .createdAt(Utils.getCurrentMillis())
                    .build();
            wholesaleFuturePlansRepository.save(wholesalerFuturePlan);
        }else { // Going to assign plan directly to user.
            logger.info("Going to assign this plan as user current plan.");
            Integer months = plan.getMonths();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(currentMillis);
            calendar.add(Calendar.MONTH, months);
            long expiryDate = calendar.getTimeInMillis();
            WholesalerPlans userPlans = WholesalerPlans.builder()
                    .slug(UUID.randomUUID().toString())
                    .userId(userId)
                    .servicePlan(plan)
                    .createdAt(currentMillis)
                    .expiryDate(expiryDate)
                    .createdBy(userId)
                    .build();
            WholesalerPlans userPlan = wholesaleUserPlansRepository.save(userPlans); // Create operation
            int updated = wholesaleUserHbRepository.updateUserActivePlan(userId, userPlan.getId());// Update operation
            if(updated < 1){
                throw new NotFoundException("No user found. to assign this plan.");
            }
        }
        logger.info("Completed assignUserPlan method");
    }

    public Page<WholesalerPlans> getAllUserPlans(User loggedUser, UserPlanDto searchFilters) {
        logger.info("Starting getAllUserPlans method with loggedUser: {}, searchFilters: {}", loggedUser, searchFilters);
        Specification<WholesalerPlans> specification = Specification.where(
                hasSlug(searchFilters.getSlug())
                .and(greaterThanOrEqualCreatedFromDate(searchFilters.getCreatedFromDate()))
                .and(lessThanOrEqualToCreatedToDate(searchFilters.getCreatedToDate()))
                .and(greaterThanOrEqualExpiredFromDate(searchFilters.getExpiredFromDate()))
                .and(lessThanOrEqualToExpiredToDate(searchFilters.getExpiredToDate()))
                .and(isStatus(searchFilters.getStatus()))
                .and(isUserId(loggedUser.getId()))
        );
        Pageable pageable = getPageable(searchFilters);
        Page<WholesalerPlans> userPlans = wholesaleUserPlansRepository.findAll(specification, pageable);
        logger.info("Completed getAllUserPlans method");
        return userPlans;
    }

    public ServicePlan getDefaultServicePlan() {
        logger.info("Starting getDefaultServicePlan method");
        ServicePlan defaultServicePlan = wholesaleServicePlanRepository.getDefaultServicePlan();
        logger.info("Completed getDefaultServicePlan method");
        return defaultServicePlan;
    }

}
