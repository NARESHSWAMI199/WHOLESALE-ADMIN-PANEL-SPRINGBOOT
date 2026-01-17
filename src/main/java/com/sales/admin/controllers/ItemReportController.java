package com.sales.admin.controllers;


import com.sales.admin.services.ItemReportService;
import com.sales.dto.SearchFilters;
import com.sales.entities.ItemReport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin/item/report/")
@RequiredArgsConstructor
public class ItemReportController {

    private final ItemReportService itemReportService;
    @PostMapping("all")
    @PreAuthorize("hasAuthority('item.report.all')")
    public ResponseEntity<Page<ItemReport>> findAllItemsReports(@RequestBody SearchFilters searchFilters){
        Page<ItemReport> itemReports = itemReportService.getAllReportByItemId(searchFilters);
        return new ResponseEntity<>(itemReports, HttpStatusCode.valueOf(200));
    }

}
