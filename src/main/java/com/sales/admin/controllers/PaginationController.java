package com.sales.admin.controllers;


import com.sales.admin.services.PaginationService;
import com.sales.dto.UserPaginationDto;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/pagination/")
@RequiredArgsConstructor
public class PaginationController  {

    private final PaginationService paginationService;

    @GetMapping("all")
    public ResponseEntity<Map<String,Object>> findAllUserPaginations(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> allUserPaginations = paginationService.findUserPaginationsByUserId(loggedUser);
        return new ResponseEntity<>(allUserPaginations, HttpStatus.valueOf(200));
    }


    @PostMapping("update")
    public ResponseEntity<Map<String,Object>> updatePaginationRowNumber(HttpServletRequest request, @RequestBody UserPaginationDto userPaginationDto) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        userPaginationDto.setUserId(loggedUser.getId());
        int updated = paginationService.updateUserPaginationRowsNumber(userPaginationDto);
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
