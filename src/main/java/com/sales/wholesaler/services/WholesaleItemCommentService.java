package com.sales.wholesaler.services;

import com.sales.dto.ItemCommentsFilterDto;
import com.sales.entities.ItemComments;
import com.sales.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sales.specifications.ItemCommentSpecifications.*;


@Service
public class WholesaleItemCommentService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemCommentService.class);

    public List<ItemComments> getALlItemComment(ItemCommentsFilterDto filters, User loggedUser) {
        logger.info("Starting getALlItemComment method with filters: {}, loggedUser: {}", filters, loggedUser);
        if(filters.getItemId() == 0) {
            logger.error("Invalid itemId provided");
            throw new IllegalArgumentException("Please provide a valid itemId.");
        }
        Specification<ItemComments> specification = Specification.where(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
                        .and(isItemId(filters.getItemId()))
                        .and(isParentComment(filters.getParentId()))
        );
        Pageable pageable = getPageable(filters);
        Page<ItemComments> itemComments = wholesaleItemCommentRepository.findAll(specification,pageable);
        List<ItemComments> content = itemComments.getContent();
        for(ItemComments comment : content) comment.setRepliesCount(wholesaleItemCommentRepository.totalReplies(comment.getId()));
        logger.info("Completed getALlItemComment method");
        return content;
    }


}
