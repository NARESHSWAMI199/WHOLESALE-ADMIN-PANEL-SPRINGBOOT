package com.sales.admin.services;


import com.sales.admin.repositories.ItemReportRepository;
import com.sales.dto.SearchFilters;
import com.sales.entities.ItemReport;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.helpers.PaginationHelper.getPageable;
import static com.sales.specifications.ItemsReportSpecifications.hasItemId;

@Service
@AllArgsConstructor
public class ItemReportService {
    private final Logger logger = LoggerFactory.getLogger(ItemReportService.class);
    private final ItemReportRepository itemReportRepository;
    public Page<ItemReport> getAllReportByItemId(SearchFilters searchFilters){
        Pageable pageable = getPageable(logger,searchFilters);
        Specification<ItemReport> specification = Specification.allOf(hasItemId(searchFilters.getItemId()));
        return itemReportRepository.findAll(specification,pageable);
    }


}
