package com.sales.admin.controllers;


import com.sales.dto.ContactDto;
import com.sales.entities.ChatUser;
import com.sales.entities.Contact;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("chat-users")
public class ChatUserController extends ServiceContainer{


    @GetMapping("all")
    public ResponseEntity<List<User>> getAllChatUsers(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        List<User> allContactsByUserId = chatUserService.getAllChatUsers(loggedUser);
        return new ResponseEntity<>(allContactsByUserId, HttpStatus.valueOf(200));
    }


    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> addNewChatUser(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        ChatUser chatUser = chatUserService.addNewChatUser(loggedUser, contactDto.getContactSlug());
        if(chatUser != null){
            result.put("message","Your chat user has been successfully inserted");
            result.put("status", 200);
        }else {
            result.put("message","Something went wrong during insert your chat user");
            result.put("status", 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}
