package com.sales.admin.controllers;

import com.sales.entities.City;
import com.sales.entities.State;
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
@RequestMapping("/admin/address")
public class AddressController extends ServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @GetMapping("/city/{stateId}")
    public ResponseEntity<List<City>> getCityList(@PathVariable Integer stateId) {
        logger.info("Fetching city list for stateId: {}", stateId);
        List<City> cityList = addressService.getCityList(stateId);
        return new ResponseEntity<>(cityList, HttpStatus.OK);
    }

    @GetMapping("/state")
    public ResponseEntity<List<State>> getStateList() {
        logger.info("Fetching state list");
        List<State> stateList = addressService.getStateList();
        return new ResponseEntity<>(stateList, HttpStatus.OK);
    }

}
