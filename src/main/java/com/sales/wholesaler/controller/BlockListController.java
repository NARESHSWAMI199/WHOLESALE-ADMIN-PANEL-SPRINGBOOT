package com.sales.wholesaler.controller;

import com.sales.entities.BlockedUser;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import com.sales.wholesaler.services.BlockListService;
import com.sales.wholesaler.services.WholesaleUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlockListController  {

  
  private static final Logger logger = LoggerFactory.getLogger(BlockListController.class);
    private final BlockListService blockListService;
    private final WholesaleUserService wholesaleUserService;

    @GetMapping("/block/{recipient}")
    public ResponseEntity<Map<String,Object>> addUserInBlockList(@PathVariable String recipient, HttpServletRequest request){
        logger.debug("Blocking user: {}", recipient);
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        BlockedUser blockedUser = blockListService.addAUserInBlockList(loggedUser, recipient);
        if(blockedUser.getId() > 0){
            result.put(ConstantResponseKeys.MESSAGE,"User has been successfully blocked");
            result.put(ConstantResponseKeys.STATUS,200);
        }else {
            result.put(ConstantResponseKeys.MESSAGE,"Something went wrong during block user");
            result.put(ConstantResponseKeys.STATUS, 400);
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }



    @GetMapping("/unblock/{recipient}")
    public ResponseEntity<Map<String,Object>> removeUserFromBlockList(@PathVariable String recipient, HttpServletRequest request){
        logger.debug("Unblocking user: {}", recipient);
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        boolean unblocked = blockListService.removeUserFromBlockList(loggedUser.getId(), recipient);
        if(unblocked){
            result.put(ConstantResponseKeys.MESSAGE,"User has been successfully unblocked");
            result.put(ConstantResponseKeys.STATUS,200);
        }else {
            result.put(ConstantResponseKeys.MESSAGE,"Something went wrong during unblock user");
            result.put(ConstantResponseKeys.STATUS, 400);
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }


    @GetMapping("is-blocked/{receiverSlug}")
    public ResponseEntity<Boolean> isReceiverBlocked(@PathVariable String receiverSlug, HttpServletRequest request) {
        User loggedUser = (User) request.getAttribute("user");
        User receiver = wholesaleUserService.findUserBySlug(receiverSlug);
        boolean blocked = blockListService.isReceiverBlockedBySender(loggedUser, receiver);
        return new ResponseEntity<>(blocked,HttpStatus.valueOf(200));
    }

}
