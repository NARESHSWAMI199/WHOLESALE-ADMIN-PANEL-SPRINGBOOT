package com.sales.admin.services;


import com.sales.dto.SearchFilters;
import com.sales.entities.ItemReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.specifications.ItemsReportSpecifications.hasItemId;

@Service
public class ItemReportService extends RepoContainer {

    public Page<ItemReport> getAllReportByItemId(SearchFilters searchFilters){
        Pageable pageable = getPageable(searchFilters);
        Specification<ItemReport> specification = Specification.where(hasItemId(searchFilters.getItemId()));
        return itemReportRepository.findAll(specification,pageable);
    }


}
