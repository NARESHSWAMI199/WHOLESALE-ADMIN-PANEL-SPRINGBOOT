package com.sales.wholesaler.services;

import com.sales.dto.ItemReviewsFilterDto;
import com.sales.entities.ItemReviews;
import com.sales.entities.User;
import com.sales.wholesaler.repository.WholesaleItemReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.helpers.PaginationHelper.getPageable;
import static com.sales.specifications.ItemReviewSpecifications.*;


@Service
@RequiredArgsConstructor
public class WholesaleItemReviewService  {

    private final WholesaleItemReviewRepository wholesaleItemReviewRepository;
    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemReviewService.class);

    public Page<ItemReviews> getAllItemReview(ItemReviewsFilterDto filters, User loggedUser) {
        logger.debug("Starting getALlItemReview method with filters: {}, loggedUser: {}", filters, loggedUser);
        if(filters.getItemId() == 0) {
            logger.error("Invalid itemId provided");
            throw new IllegalArgumentException("Please provide a valid itemId.");
        }
        Specification<ItemReviews> specification = Specification.allOf(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
                        .and(isItemId(filters.getItemId()))
                        .and(isParentComment(filters.getParentId()))
        );
        Pageable pageable = getPageable(logger,filters);
        return wholesaleItemReviewRepository.findAll(specification,pageable);
    }


}
