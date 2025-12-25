package com.sales.admin.services;


import com.sales.dto.SearchFilters;
import com.sales.entities.StoreReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.sales.specifications.StoreReportSpecifications.hasStoreId;

@Service
public class StoreReportService extends  RepoContainer{

    public Page<StoreReport> getAllReportByStoreId(SearchFilters searchFilters){
        Pageable pageable = getPageable(searchFilters);
        Specification<StoreReport> specification = Specification.allOf(hasStoreId(searchFilters.getStoreId()));
        return storeReportRepository.findAll(specification,pageable);
    }


}
