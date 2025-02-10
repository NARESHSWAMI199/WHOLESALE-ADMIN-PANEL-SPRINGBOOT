package com.sales.admin.controllers;


import com.sales.dto.DeleteDto;
import com.sales.dto.GroupDto;
import com.sales.dto.SearchFilters;
import com.sales.entities.Group;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("group")
public class GroupController extends ServiceContainer {


    @PostMapping("/all")
    public ResponseEntity<Page<Group>> getAllGroup( HttpServletRequest request,@RequestBody SearchFilters searchFilters){
        User loggedUser =  (User)request.getAttribute("user");
        Page<Group> storePage =  groupService.getAllGroups(searchFilters,loggedUser);
        return new ResponseEntity<>(storePage, HttpStatus.OK);
    }


    @GetMapping("/permissions/all")
    public ResponseEntity<Map<String,List<Object>>> getAllPermissions(HttpServletRequest request){
        Map<String,List<Object>> permissions =  groupService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }



    @Transactional
    @PostMapping(value = {"create", "update"})
    public ResponseEntity<Map<String,Object>> createOrUpdate(HttpServletRequest request, @RequestBody GroupDto groupDto) throws Exception {
        User loggedUser =  (User)request.getAttribute("user");
        String path = request.getRequestURI();
        Map<String,Object> response= groupService.createOrUpdateGroup(groupDto,loggedUser,path);
        return new ResponseEntity<>(response, HttpStatus.valueOf((Integer) response.get("status")));
    }




    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getDetailGroup(@PathVariable String slug) {
        Map responseObj = new HashMap();
        Map<String,Object> group = groupService.findGroupBySlug(slug);
        responseObj.put("res", group);
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }


    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteGroupBySlug(HttpServletRequest request,@RequestBody DeleteDto deleteDto) throws Exception {
        Map<String,Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isUpdated = groupService.deleteGroupBySlug(deleteDto,loggedUser);
        if (isUpdated > 0) {
            responseObj.put("message", "User has been successfully deleted.");
            responseObj.put("status", 201);
        } else {
            responseObj.put("message", "No group found to delete");
            responseObj.put("status", 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



}
