package com.sales.wholesaler.controller;


import com.sales.dto.SearchFilters;
import com.sales.entities.User;
import com.sales.entities.WholesalerFuturePlans;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("future/plans")
public class WholesaleFuturePlansController extends WholesaleServiceContainer {



    @PostMapping("/")
    public ResponseEntity<Page<WholesalerFuturePlans>> getAllWholesalerFuturePlans(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        User user = (User) request.getAttribute("user");
        Page<WholesalerFuturePlans> wholesalerFuturePlans = wholesaleFuturePlansService.getWholesalerFuturePlans(user,searchFilters);
        return new ResponseEntity<>(wholesalerFuturePlans, HttpStatusCode.valueOf(200));
    }




}
