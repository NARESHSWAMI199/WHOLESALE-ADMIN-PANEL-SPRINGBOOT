package com.sales.admin.services;

import com.sales.dto.ItemCommentsFilterDto;
import com.sales.dto.UserSearchFilters;
import com.sales.entities.ItemComments;
import com.sales.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sales.specifications.ItemCommentSpecifications.*;


@Service
public class ItemCommentService extends  RepoContainer {

    public List<ItemComments> getALlItemComment(ItemCommentsFilterDto filters) {
        Specification<ItemComments> specification = Specification.where(
                (containsName(filters.getSearchKey()))
                        .and(greaterThanOrEqualFromDate(filters.getFromDate()))
                        .and(lessThanOrEqualToToDate(filters.getToDate()))
                        .and(hasSlug(filters.getSlug()))
                        .and(isItemId(filters.getItemId()))
                        .and(isParentComment(filters.getParentId()))
        );
        Pageable pageable = getPageable(filters);
        Page<ItemComments> itemComments = itemCommentRepository.findAll(specification,pageable);
        List<ItemComments> content = itemComments.getContent();
        for(ItemComments comment : content) comment.setRepliesCount(itemCommentRepository.totalReplies(comment.getId()));
        return content;
    }


}
