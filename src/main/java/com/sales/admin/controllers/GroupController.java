package com.sales.admin.controllers;


import com.sales.admin.services.GroupService;
import com.sales.dto.DeleteDto;
import com.sales.dto.GroupDto;
import com.sales.dto.SearchFilters;
import com.sales.entities.Group;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("group")
@RequiredArgsConstructor
public class GroupController  {

    private final GroupService groupService;
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @PostMapping("/all")
    public ResponseEntity<Page<Group>> getAllGroup(HttpServletRequest request, @RequestBody SearchFilters searchFilters) {
        logger.debug("Fetching all groups with filters: {}", searchFilters);
        User loggedUser = (User) request.getAttribute("user");
        Page<Group> storePage = groupService.getAllGroups(searchFilters, loggedUser);
        return new ResponseEntity<>(storePage, HttpStatus.OK);
    }

    @GetMapping("/permissions/all")
    public ResponseEntity<Map<String, List<Object>>> getAllPermissions(HttpServletRequest request) {
        logger.debug("Fetching all permissions");
        Map<String, List<Object>> permissions = groupService.getAllPermissions();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = {"create", "update"})
    public ResponseEntity<Map<String, Object>> createOrUpdate(HttpServletRequest request, @RequestBody GroupDto groupDto) throws Exception {
        logger.debug("Creating or updating group: {}", groupDto);
        User loggedUser = (User) request.getAttribute("user");
        String path = request.getRequestURI();
        Map<String, Object> response = groupService.createOrUpdateGroup(groupDto, loggedUser, path);
        return new ResponseEntity<>(response, HttpStatus.valueOf((Integer) response.get(ConstantResponseKeys.STATUS)));
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getDetailGroup(@PathVariable String slug) {
        logger.debug("Fetching group details for slug: {}", slug);
        Map<String, Object> responseObj = new HashMap<>();
        Map<String, Object> group = groupService.findGroupBySlug(slug);
        responseObj.put(ConstantResponseKeys.RES, group);
        responseObj.put(ConstantResponseKeys.STATUS, 200);
        return new ResponseEntity<>(responseObj, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteGroupBySlug(HttpServletRequest request, @RequestBody DeleteDto deleteDto) throws Exception {
        logger.debug("Deleting group with slug: {}", deleteDto);
        Map<String, Object> responseObj = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isUpdated = groupService.deleteGroupBySlug(deleteDto, loggedUser);
        if (isUpdated > 0) {
            responseObj.put(ConstantResponseKeys.MESSAGE, "User has been successfully deleted.");
            responseObj.put(ConstantResponseKeys.STATUS, 200);
        } else {
            responseObj.put(ConstantResponseKeys.MESSAGE, "No group found to delete");
            responseObj.put(ConstantResponseKeys.STATUS, 404);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get(ConstantResponseKeys.STATUS)));
    }

}
