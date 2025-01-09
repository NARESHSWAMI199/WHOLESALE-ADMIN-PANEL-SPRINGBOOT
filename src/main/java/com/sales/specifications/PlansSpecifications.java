package com.sales.specifications;

import com.sales.entities.User;
import com.sales.entities.UserPlans;
import com.sales.entities.UserPlans_;
import com.sales.entities.User_;
import org.springframework.data.jpa.domain.Specification;

public class PlansSpecifications {


    /**
     * there we create some predicates which is use for filters
     */


    public static Specification<User> containsName(String searchKey) {
        return (root, query, criteriaBuilder) -> {
            if (searchKey == null) return null;
            return criteriaBuilder.like(root.get(User_.USERNAME), "%" + searchKey + "%");
        };
    }


    public static Specification<UserPlans> greaterThanOrEqualCreatedFromDate(Long createdFromDate) {
        return (root, query, criteriaBuilder) -> {
            if (createdFromDate == null) return  null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get(UserPlans_.CREATED_AT), createdFromDate);
        };
    }

    public static Specification<UserPlans> lessThanOrEqualToCreatedToDate(Long createdToDate) {
        return (root, query, criteriaBuilder) -> {
            if (createdToDate == null) return null;
            return criteriaBuilder.lessThanOrEqualTo(root.get(UserPlans_.CREATED_AT), createdToDate);
        };
    }




    public static Specification<UserPlans> greaterThanOrEqualExpiredFromDate(Long expiredFromDate) {
        return (root, query, criteriaBuilder) -> {
            if (expiredFromDate == null) return  null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get(UserPlans_.EXPIRY_DATE), expiredFromDate);
        };
    }

    public static Specification<UserPlans> lessThanOrEqualToExpiredToDate(Long expiredToDate) {
        return (root, query, criteriaBuilder) -> {
            if (expiredToDate == null) return null;
            return criteriaBuilder.lessThanOrEqualTo(root.get(UserPlans_.EXPIRY_DATE), expiredToDate);
        };
    }


    /* Expired or not */
    public static Specification<UserPlans> isStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null){
                return null;
            } else if (status.equals("A")) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(UserPlans_.createdAt),root.get(UserPlans_.expiryDate));
            }
            return criteriaBuilder.lessThan(root.get(UserPlans_.createdAt),root.get(UserPlans_.expiryDate));
        };
    }

    public static Specification<UserPlans> hasSlug(String slug) {
        return (root, query, criteriaBuilder) -> {
            if (slug == null || slug.isEmpty()) return null;
            return criteriaBuilder.equal(root.get(UserPlans_.SLUG), slug.trim());
        };
    }

    public static Specification<UserPlans> isUserId(Integer userId){
        return (root, query, criteriaBuilder) -> {
            if (userId == null) return null;
            return criteriaBuilder.equal(root.get(UserPlans_.USER_ID), userId);
        };
    }

}
