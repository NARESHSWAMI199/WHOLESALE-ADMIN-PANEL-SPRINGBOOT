package com.sales.wholesaler.controller;

import com.sales.entities.BlockedUser;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BlockListController extends WholesaleServiceContainer {

    @GetMapping("/block/{recipient}")
    public ResponseEntity<Map<String,Object>> addUserInBlockList(@PathVariable String recipient, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        BlockedUser blockedUser = blockListService.addAUserInBlockList(loggedUser, recipient);
        if(blockedUser.getId() > 0){
            result.put("message","User has been successfully blocked");
            result.put("status",200);
        }else {
            result.put("message","Something went wrong during block user");
            result.put("status", 400);
        }
        return new ResponseEntity(result, HttpStatus.valueOf((Integer) result.get("status")));

    }

}
