package com.sales.admin.controllers;


import com.sales.dto.StatusDto;
import com.sales.dto.UserDto;
import com.sales.dto.UserSearchFilters;
import com.sales.entities.User;
import com.sales.jwtUtils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("admin/auth")
public class UserController extends ServiceContainer {

    @Autowired
    JwtToken jwtToken;

    @PostMapping("/all")
    public ResponseEntity<Page<User>> getAllUsers(@RequestBody UserSearchFilters searchFilters, HttpServletRequest httpServletRequest) {
        Page<User> userPage = userService.getAllUser(searchFilters);
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> findByEmailAndPassword(@RequestBody UserDto userDetails) {
        logger.info("====================== ADMIN LOGIN PROCESS STARTED ======================");
        Map<String, Object> responseObj = new HashMap<>();
        User user = userService.findByEmailAndPassword(userDetails);
        if (user == null) {
            responseObj.put("message", "invalid credentials.");
            responseObj.put("status", 401);
            return new ResponseEntity<>(responseObj, HttpStatus.UNAUTHORIZED);
        } else if (user.getStatus().equalsIgnoreCase("A")) {
            responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
            responseObj.put("message", "success");
            responseObj.put("status", 200);
            responseObj.put("user", user);
            return new ResponseEntity<>(responseObj, HttpStatus.OK);
        } else {
            responseObj.put("message", "You are blocked by admin");
            responseObj.put("status", 401);
            return new ResponseEntity<>(responseObj, HttpStatus.OK);
        }
    }


    @Transactional
    @PostMapping(value = {"/add", "/update"})
    public ResponseEntity<Map<String, Object>> register(HttpServletRequest request, @RequestBody UserDto userDto) {
        Map responseObj = new HashMap();
        try {
            User logggedUser = (User) request.getAttribute("user");
            responseObj = userService.createOrUpdateUser(userDto, logggedUser);
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<Map<String, Object>> getDetailUser(@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        User user = userService.getUserDetail(slug);
        if (user != null) {
            responseObj.put("res", user);
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "Please check you parameters not a valid request.");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

    @Transactional
    @GetMapping("/delete/{slug}")
    public ResponseEntity<Map<String, Object>> deleteUserBySlug(@PathVariable String slug) {
        Map<String,Object> responseObj = new HashMap<>();
        int isUpdated = userService.deleteUserBySlug(slug);
        if (isUpdated > 0) {
            responseObj.put("message", "User has been successfully deleted.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }


    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> stockSlug(@RequestBody StatusDto statusDto) {
        Map<String,Object> responseObj = new HashMap<>();
        int isUpdated = userService.updateStatusBySlug(statusDto);
        if (isUpdated > 0) {
            responseObj.put("message", "Item's status has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to delete.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

}




