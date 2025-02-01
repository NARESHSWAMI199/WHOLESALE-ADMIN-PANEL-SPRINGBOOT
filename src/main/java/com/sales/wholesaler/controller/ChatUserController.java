package com.sales.wholesaler.controller;


import com.sales.admin.controllers.ServiceContainer;
import com.sales.dto.ChatUserDto;
import com.sales.dto.ContactDto;
import com.sales.entities.ChatUser;
import com.sales.entities.User;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("chat-users")
public class ChatUserController extends WholesaleServiceContainer {


    @GetMapping("all")
    public ResponseEntity<List<ChatUser>> getAllChatUsers(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        List<ChatUser> allContactsByUserId = chatUserService.getAllChatUsers(loggedUser,request);
        return new ResponseEntity<>(allContactsByUserId, HttpStatus.valueOf(200));
    }


    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> addNewChatUser(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        ChatUser chatUser = chatUserService.addNewChatUser(loggedUser, contactDto.getContactSlug(),"A");
        if(chatUser != null){
            result.put("message","Your chat user has been successfully inserted");
            result.put("status", 200);
        }else {
            result.put("message","Something went wrong during insert your chat user");
            result.put("status", 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }


    @PostMapping("/accept")
    public ResponseEntity<Map<String,Object>> updateChatAcceptStatus(HttpServletRequest request, @RequestBody ChatUserDto chatUserDto) {
        User loggedUser = (User) request.getAttribute("user");
        Map<String,Object> result = new HashMap<>();

        boolean accepted = chatUserService.updateAcceptStatus(loggedUser.getId(), chatUserDto.getReceiverSlug(), chatUserDto.getStatus());
        String status = "accepted";
        if(!Utils.isEmpty(chatUserDto.getReceiverSlug()) && chatUserDto.getReceiverSlug().equals("R")){
            status = "declined";
        }
        if(accepted){
            result.put("message","Chat "+status+" .");
            result.put("status",200);
        }else {
            result.put("message","Something went wrong during "+status+" chat.");
            result.put("status",400);
        }

        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status"))) ;
    }

}
