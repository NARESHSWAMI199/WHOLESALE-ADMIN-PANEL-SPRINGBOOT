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
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("group")
public class GroupController extends ServiceContainer {


    @PostMapping("/all")
    public ResponseEntity<Page<Group>> getAllGroup( HttpServletRequest request,@RequestBody SearchFilters searchFilters){
        User loggedUser =  (User)request.getAttribute("user");
        Page<Group> storePage =  groupService.getALlGroups(searchFilters,loggedUser);
        return new ResponseEntity<>(storePage, HttpStatus.OK);
    }


    @RequestMapping("/permissions/all")
    public ResponseEntity<Map<String,List<Object>>> getAllPermissions(HttpServletRequest request){
        Map<String,List<Object>> permissions =  groupService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }



    @Transactional
    @PostMapping(value = {"create", "update"})
    public ResponseEntity<Map<String,Object>> createOrUpdate(HttpServletRequest request, @RequestBody GroupDto groupDto) throws Exception {
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
            responseObj.put("message", "There is no permission at this time.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @Transactional
    @GetMapping("/delete/{slug}")
    public ResponseEntity<Map<String, Object>> deleteGroupBySlug(@PathVariable String slug) throws Exception {
        Map<String,Object> responseObj = new HashMap<>();
        int isUpdated = groupService.deleteGroupBySlug(slug);
        if (isUpdated > 0) {
            responseObj.put("message", "User has been successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete. recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



}