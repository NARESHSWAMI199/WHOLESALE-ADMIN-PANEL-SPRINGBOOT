package com.sales.admin.controllers;


import com.sales.dto.GroupDto;
import com.sales.dto.SearchFilters;
import com.sales.entities.Group;
import com.sales.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("group")
public class GroupController extends ServiceContainer {


    @PostMapping("/all")
    public ResponseEntity<Page<Group>> getAllGroup(@RequestBody SearchFilters searchFilters){
        Page<Group> storePage =  groupService.getALlGroups(searchFilters);
        return new ResponseEntity<>(storePage, HttpStatus.OK);
    }


    @RequestMapping("/permission/all")
    public ResponseEntity<Map<String,List<Object>>> getAllPermissions(){
        Map<String,List<Object>> permissions =  groupService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }



    @PostMapping(value = {"create", "update"})
    public ResponseEntity<Map<String,Object>> createOrUpdate(HttpServletRequest request, @RequestBody GroupDto groupDto){
        User loggedUser =  (User)request.getAttribute("user");
         Map<String,Object> response= groupService.createOrUpdateGroup(groupDto,loggedUser);
         return new ResponseEntity<Map<String,Object>>(response, HttpStatus.valueOf((Integer) response.get("status")));
    }




    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getDetailGroup(@PathVariable String slug) {
        Map responseObj = new HashMap();
        Map<String,Object> groupPermission = groupService.findGroupBySlug(slug);
        if (groupPermission !=null && groupPermission.size() > 0) {
            responseObj.put("res", groupPermission);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "Please check you parameters not a valid request.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


}
