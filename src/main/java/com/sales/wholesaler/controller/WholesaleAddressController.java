package com.sales.wholesaler.controller;


import com.sales.entities.City;
import com.sales.entities.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wholesale/address")
public class WholesaleAddressController extends WholesaleServiceContainer {

    @GetMapping("/city/{stateId}")
    public ResponseEntity<List<City>> getCityList(@PathVariable Integer stateId ) {
        List<City> cityList = addressService.getCityList(stateId);
        return new ResponseEntity<>(cityList, HttpStatus.OK);
    }

    @GetMapping("/state")
    public ResponseEntity<List<State>> getStateList() {
        List<State> stateList = addressService.getStateList();
        return new ResponseEntity<>(stateList,HttpStatus.OK);
    }


}
