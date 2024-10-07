package com.sales.wholesaler.controller;


import com.sales.dto.PasswordDto;
import com.sales.dto.UserDto;
import com.sales.entities.Store;
import com.sales.entities.User;
import com.sales.global.GlobalConstant;
import com.sales.jwtUtils.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wholesale/auth")
public class WholesaleUserController extends WholesaleServiceContainer {

    @Autowired
    JwtToken jwtToken;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> findByEmailAndPassword(@RequestBody Map<String,String> param) {
        try {
            logger.info("=============LOGIN PROCESSES STARTED =====================");
            Map<String, Object> responseObj = new HashMap<>();
            User user = wholesaleUserService.findByEmailAndPassword(param);
            if (user == null) {
                responseObj.put("message", "invalid credentials.");
                responseObj.put("status", 401);
                return new ResponseEntity<>(responseObj, HttpStatus.UNAUTHORIZED);
            } else if (user.getStatus().equalsIgnoreCase("A")) {
                responseObj.put("token", "Bearer " + jwtToken.generateToken(user));
                Store store = wholesaleStoreService.getStoreByUserId(user.getId());
                responseObj.put("message", "success");
                responseObj.put("status", 200);
                responseObj.put("user", user);
                responseObj.put("store", store);
                return new ResponseEntity<>(responseObj, HttpStatus.OK);
            } else {
                responseObj.put("message", "You are blocked by admin");
                responseObj.put("status", 401);
                return new ResponseEntity<>(responseObj, HttpStatus.OK);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());;
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    @PostMapping(value = {"/update"})
    public ResponseEntity<Map<String, Object>> updateAuth(HttpServletRequest request, @RequestBody UserDto userDto) {
        Map<String,Object> responseObj = new HashMap<>();
        try {
            User logggedUser = (User) request.getAttribute("user");
            responseObj = wholesaleUserService.updateUserProfile(userDto, logggedUser);
        } catch (Exception e) {
            responseObj.put("message", e.getMessage());
            responseObj.put("status", 500);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));

    }


    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getDetailUser(HttpServletRequest request) {
        Map<String,Object> responseObj = new HashMap<>();
        User user = (User) request.getAttribute("user");
        Store store = wholesaleStoreService.getStoreByUserSlug(user.getId());
        responseObj.put("store", store);
        responseObj.put("user", user);
        responseObj.put("status", 200);
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }



    @Transactional
    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> resetUserPasswordBySlug(HttpServletRequest request ,@RequestBody PasswordDto passwordDto) {
        Map<String,Object> responseObj = new HashMap<>();
        User logggedUser = (User) request.getAttribute("user");
        int isUpdated = wholesaleUserService.resetPasswordByUserSlug(passwordDto,logggedUser);
        if (isUpdated > 0 || logggedUser.getId() == GlobalConstant.suId) {
            responseObj.put("message", "User password has been successfully updated.");
            responseObj.put("status", 200);
        } else {
            responseObj.put("message", "There is nothing to update.recheck you parameters");
            responseObj.put("status", 400);
        }
        return new ResponseEntity<>(responseObj, HttpStatus.valueOf((Integer) responseObj.get("status")));
    }

}




