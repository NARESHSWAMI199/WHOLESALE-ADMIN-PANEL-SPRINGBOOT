package com.sales.wholesaler.controller;


import com.sales.dto.UserPaginationDto;
import com.sales.entities.Pagination;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wholesale/pagination/")
public class WholesalePaginationController extends WholesaleServiceContainer {

    @GetMapping("all")
    public ResponseEntity<List<Pagination>> findAllPaginations(){
        List<Pagination> allPaginations = wholesalePaginationService.findAllPaginations();
        return new ResponseEntity<>(allPaginations, HttpStatus.valueOf(200));
    }


    @PostMapping("update")
    public ResponseEntity<Map<String,Object>> updatePaginationRowNumber(HttpServletRequest request, @RequestBody UserPaginationDto userPaginationDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        userPaginationDto.setUserId(loggedUser.getId());
        int updated = wholesalePaginationService.updateUserPaginationRowsNumber(userPaginationDto);
        if(updated > 0) {
            responseObj.put("message","Pagination updated successfully");
            responseObj.put("status",200);
        }else{
            responseObj.put("message","No record found to update.");
            responseObj.put("status",404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf(200));
    }




}
