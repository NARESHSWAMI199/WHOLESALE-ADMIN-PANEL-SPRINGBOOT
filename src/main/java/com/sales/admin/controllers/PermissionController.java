package com.sales.admin.controllers;


import com.sales.dto.GroupDto;
import com.sales.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class PermissionController extends ServiceContainer {


    @PostMapping(value = {"create", "update"})
    public ResponseEntity<Map<String,Object>> createOrUpdate(HttpServletRequest request, @RequestBody GroupDto groupDto){
        User loggedUser =  (User)request.getAttribute("user");
         Map<String,Object> response= permissionService.createOrUpdateGroup(groupDto,loggedUser);
         return new ResponseEntity<Map<String,Object>>(response, HttpStatus.valueOf((Integer) response.get("status")));
    }


}
