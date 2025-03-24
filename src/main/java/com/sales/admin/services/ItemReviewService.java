package com.sales.admin.services;

import com.sales.dto.ItemReviewsFilterDto;
import com.sales.entities.ItemReviews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.specifications.ItemReviewSpecifications.*;


@Service
public class ItemReviewService extends  RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(ItemReviewService.class);

    public Page<ItemReviews> getAllItemReview(ItemReviewsFilterDto filters) {
        logger.info("Entering getALlItemReview with filters: {}", filters);
        Specification<ItemReviews> specification = Specification.where(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
                        .and(isItemId(filters.getItemId()))
                        .and(isParentComment(filters.getParentId()))
        );
        Pageable pageable = getPageable(filters);
        return itemReviewRepository.findAll(specification,pageable);
    }


}
