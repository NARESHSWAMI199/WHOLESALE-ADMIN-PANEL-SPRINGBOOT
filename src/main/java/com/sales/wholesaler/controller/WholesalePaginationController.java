package com.sales.wholesaler.controller;


import com.sales.dto.UserPaginationDto;
import com.sales.entities.SalesUser;
import com.sales.global.ConstantResponseKeys;
import com.sales.wholesaler.services.WholesalePaginationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wholesale/pagination/")
@RequiredArgsConstructor
public class WholesalePaginationController  {

    private final WholesalePaginationService wholesalePaginationService;
    @GetMapping("all")
    public ResponseEntity<Map<String,Object>> findUserPaginationSetting(Authentication authentication,HttpServletRequest request){
        SalesUser loggedUser = (SalesUser) authentication.getPrincipal();
        Map<String,Object> allUserPaginations = wholesalePaginationService.findUserPaginationsByUserId(loggedUser);
        return new ResponseEntity<>(allUserPaginations, HttpStatus.valueOf(200));
    }


    @PostMapping("update")
    public ResponseEntity<Map<String,Object>> updatePaginationRowNumber(Authentication authentication, HttpServletRequest request, @RequestBody UserPaginationDto userPaginationDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        SalesUser loggedUser = (SalesUser) authentication.getPrincipal();
        userPaginationDto.setUserId(loggedUser.getId());
        int updated = wholesalePaginationService.updateUserPaginationRowsNumber(userPaginationDto);
        if(updated > 0) {
            responseObj.put(ConstantResponseKeys.MESSAGE,"Pagination updated successfully");
            responseObj.put(ConstantResponseKeys.STATUS,200);
        }else{
            responseObj.put(ConstantResponseKeys.MESSAGE,"No record found to update.");
            responseObj.put(ConstantResponseKeys.STATUS,404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get(ConstantResponseKeys.STATUS)));
    }




}
