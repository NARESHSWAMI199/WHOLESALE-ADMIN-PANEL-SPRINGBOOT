package com.sales.wholesaler.services;

import com.sales.dto.ItemReviewsFilterDto;
import com.sales.entities.ItemReviews;
import com.sales.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sales.specifications.ItemReviewSpecifications.*;


@Service
public class WholesaleItemReviewService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemReviewService.class);

    public List<ItemReviews> getALlItemReview(ItemReviewsFilterDto filters, User loggedUser) {
        logger.info("Starting getALlItemReview method with filters: {}, loggedUser: {}", filters, loggedUser);
        if(filters.getItemId() == 0) {
            logger.error("Invalid itemId provided");
            throw new IllegalArgumentException("Please provide a valid itemId.");
        }
        Specification<ItemReviews> specification = Specification.where(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
                        .and(isItemId(filters.getItemId()))
                        .and(isParentComment(filters.getParentId()))
        );
        Pageable pageable = getPageable(filters);
        Page<ItemReviews> ItemReviews = wholesaleItemReviewRepository.findAll(specification,pageable);
        List<ItemReviews> content = ItemReviews.getContent();
//        for(ItemReviews comment : content) comment.setRepliesCount(wholesaleItemReviewRepository.totalReplies(comment.getId()));
//        logger.info("Completed getALlItemReview method");
        return content;
    }


}
