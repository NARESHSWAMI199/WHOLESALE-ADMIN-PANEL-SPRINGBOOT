package com.sales.wholesaler.controller;


import com.sales.entities.City;
import com.sales.entities.State;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wholesale/address")
@RequiredArgsConstructor
public class WholesaleAddressController extends WholesaleServiceContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(WholesaleAddressController.class);

    @GetMapping("/city/{stateId}")
    public ResponseEntity<List<City>> getCityList(@PathVariable Integer stateId ) {
        safeLog.info(logger,"Starting getCityList method");
        List<City> cityList = addressService.getCityList(stateId);
        safeLog.info(logger,"Completed getCityList method");
        return new ResponseEntity<>(cityList, HttpStatus.OK);
    }

    @GetMapping("/state")
    public ResponseEntity<List<State>> getStateList() {
        safeLog.info(logger,"Starting getStateList method");
        List<State> stateList = addressService.getStateList();
        safeLog.info(logger,"Completed getStateList method");
        return new ResponseEntity<>(stateList,HttpStatus.OK);
    }


}
