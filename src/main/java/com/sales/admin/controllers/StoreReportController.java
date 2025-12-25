package com.sales.admin.controllers;


import com.sales.dto.SearchFilters;
import com.sales.entities.StoreReport;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/store/report/")
public class StoreReportController extends ServiceContainer {


    @PostMapping("all")
    public ResponseEntity<Page<StoreReport>> findAllItemsReports(@RequestBody SearchFilters searchFilters){
        Page<StoreReport> storeReports = storeReportService.getAllReportByStoreId(searchFilters);
        return new ResponseEntity<>(storeReports, HttpStatusCode.valueOf(200));
    }

}
