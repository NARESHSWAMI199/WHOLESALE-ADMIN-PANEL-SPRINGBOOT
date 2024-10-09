package com.sales.specifications;

import com.sales.entities.*;
import org.springframework.data.jpa.domain.Specification;

public class ItemCommentSpecifications {

    public static Specification<ItemComments> containsName(String searchKey) {
        return (root, query, criteriaBuilder) -> {
            if (searchKey == null) return null;
            return criteriaBuilder.like(root.get(ItemComments_.MESSAGE), "%" + searchKey + "%");
        };
    }


    public static Specification<ItemComments> isItemId(int itemId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(ItemComments_.ITEM_ID), itemId);
        };
    }

    public static Specification<ItemComments> isParentComment(int parentId) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(ItemComments_.PARENT_ID), parentId);
        };
    }



    public static Specification<ItemComments> greaterThanOrEqualFromDate(Long fromDate) {
            return (root, query, criteriaBuilder) -> {
                if (fromDate == null) return  null;
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ItemComments_.CREATED_AT), fromDate);
            };
        }

        public static Specification<ItemComments> lessThanOrEqualToToDate(Long toDate) {
            return (root, query, criteriaBuilder) -> {
                if (toDate == null) return null;
                return criteriaBuilder.lessThanOrEqualTo(root.get(ItemComments_.CREATED_AT), toDate);
            };
        }



        public static Specification<ItemComments> hasSlug(String slug) {
            return (root, query, criteriaBuilder) -> {
                if (slug == null || slug.isEmpty()) return null;
                return criteriaBuilder.equal(root.get(ItemComments_.SLUG), slug.trim());
            };
        }

        public static Specification<ItemComments> notSuperAdmin(User loggedUser) {
            return (root, query, criteriaBuilder) -> {
                if(loggedUser.getId() == 0) return  null;
                return criteriaBuilder.notEqual(root.get(ItemComments_.ID), 0);
            };
        }

    public static Specification<StoreNotifications> isUserId(int userId) {
        return (root, query, criteriaBuilder) -> {
            if(userId == 0) return  null;
            return criteriaBuilder.equal(root.get(StoreNotifications_.USER_ID), userId);
        };
    }

}
