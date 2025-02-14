package com.sales.wholesaler.controller;


import com.sales.dto.ChatUserDto;
import com.sales.dto.ContactDto;
import com.sales.entities.ChatUser;
import com.sales.entities.User;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("chat-users")
public class ChatUserController extends WholesaleServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(ChatUserController.class);

    @GetMapping("all")
    public ResponseEntity<List<ChatUser>> getAllChatUsers(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        logger.info("Fetching all chat users for logged user: {}", loggedUser.getId());
        List<ChatUser> allContactsByUserId = chatUserService.getAllChatUsers(loggedUser,request);
        return new ResponseEntity<>(allContactsByUserId, HttpStatus.valueOf(200));
    }


    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> addNewChatUser(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        logger.info("Adding new chat user for logged user: {}", loggedUser.getId());
        ChatUser chatUser = chatUserService.addNewChatUser(loggedUser, contactDto.getContactSlug(),"A");
        if(chatUser != null){
            logger.info("Chat user added successfully for user: {}", loggedUser.getId());
            result.put("message","Your chat user has been successfully inserted");
            result.put("status", 200);
        }else {
            logger.error("Failed to add chat user for user: {}", loggedUser.getId());
            result.put("message","Something went wrong during insert your chat user");
            result.put("status", 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("/accept")
    public ResponseEntity<Map<String,Object>> updateChatAcceptStatus(HttpServletRequest request, @RequestBody ChatUserDto chatUserDto) {
        User loggedUser = (User) request.getAttribute("user");
        logger.info("Updating chat accept status for user: {}", loggedUser.getId());
        Map<String,Object> result = new HashMap<>();

        boolean accepted = chatUserService.updateAcceptStatus(loggedUser.getId(), chatUserDto.getReceiverSlug(), chatUserDto.getStatus());
        String status = "accepted";
        if(!Utils.isEmpty(chatUserDto.getReceiverSlug()) && chatUserDto.getReceiverSlug().equals("R")){
            status = "declined";
        }
        if(accepted){
            logger.info("Chat {} successfully for user: {}", status, loggedUser.getId());
            result.put("message","Chat "+status+" .");
            result.put("status",200);
        }else {
            logger.error("Failed to {} chat for user: {}", status, loggedUser.getId());
            result.put("message","Something went wrong during "+status+" chat.");
            result.put("status",400);
        }

        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status"))) ;
    }

}
