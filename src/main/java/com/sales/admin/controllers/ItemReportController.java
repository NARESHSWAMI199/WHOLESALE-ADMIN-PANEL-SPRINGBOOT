package com.sales.admin.controllers;


import com.sales.dto.SearchFilters;
import com.sales.entities.ItemReport;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/item/report/")
public class ItemReportController extends ServiceContainer{

    @PostMapping("all")
    public ResponseEntity<Page<ItemReport>> findAllItemsReports(@RequestBody SearchFilters searchFilters){
        Page<ItemReport> itemReports = itemReportService.getAllReportByItemId(searchFilters);
        return new ResponseEntity<>(itemReports, HttpStatusCode.valueOf(200));
    }

}
