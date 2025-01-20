package com.sales.admin.controllers;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.wholesaler.controller.WholesaleServiceContainer;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController extends WholesaleServiceContainer {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    /** @Note : Make sure all @MessageMappings 's prefix is /app/ */


    @PostMapping("/chats/all")
    public ResponseEntity<Map<String, List<Chat>>> getALlUsers(@RequestBody MessageDto message , HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        message.setSender(loggedUser.getSlug());
        Map<String, List<Chat>> formatedChatList = chatService.getAllChatBySenderAndReceiverKey(message);
        return new ResponseEntity<>(formatedChatList, HttpStatus.valueOf(200));
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public MessageDto sendMessage(MessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        // Set the sender in the message
        message.setSender(sender);
        return message;
    }


    @MessageMapping("/chat/private/{recipient}")
    public void sendPrivateMessage(@DestinationVariable String recipient, MessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        System.err.println("Here : "+recipient);
        message.setSender(sender);
        message.setReceiver(recipient);
        message.setMessage(HtmlUtils.htmlEscape(message.getMessage()));
        chatService.saveMessage(message);
        if (recipient == null) throw new MyException("Please provide a valid recipient");
        /* you need to subscribe like  /user/{userId}/queue/private */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", message);
    }




    @MessageMapping("/chat/connect/{slug}")
    public void userConnected(@DestinationVariable String slug, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        System.err.println("Connected");
        try{
            String sender = simpMessageHeaderAccessor.getSessionAttributes().get("username").toString();
            User user = wholesaleUserService.findUserBySlug(slug);
            if(user == null) throw new MyException("Not valid user to connect for chat.");
            user.setOnline(true);
            wholesaleUserService.updateLastSeen(user);
            GlobalConstant.onlineUsers.put(slug, user);
        }catch (Exception e){
            logger.info(e.getMessage());
        }

    }




    @MessageMapping("/chat/deactivate/{slug}")
    public void disconnectUser(@DestinationVariable String slug) {
        System.err.println("Disconnected");
        try{
            User user = GlobalConstant.onlineUsers.get(slug);
            if(user == null) throw new MyException("Not valid user to connect for chat.");
            user.setOnline(false);
            wholesaleUserService.updateLastSeen(user);
            GlobalConstant.onlineUsers.put(slug, user);
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }



    @MessageMapping("/chats/was-seen/{recipient}")
    public void isReceiverSeen(@DestinationVariable("recipient") String recipient){
        if (recipient == null) throw new MyException("Please provide a valid recipient");
        System.err.println("Seen Called.....");
        /* you need to subscribe like  /user/{userId}/queue/private/chat/seen */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private/chat/seen",true);
    }


    @MessageMapping("/chat/{slug}/userStatus")
    public void getUserStatus(@DestinationVariable String slug, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        System.err.println("Status checking." + slug);
        String sender = (String) simpMessageHeaderAccessor.getSessionAttributes().get("username");
        /* you need to subscribe like  /user/{userId}/queue/private/status */
        messagingTemplate.convertAndSendToUser(sender, "/queue/private/status",GlobalConstant.onlineUsers.getOrDefault(slug,new User()));
    }



    @GetMapping("/chat/status/{slug}")
    public ResponseEntity<User> getUserStatus(@PathVariable String slug){
        User user = GlobalConstant.onlineUsers.getOrDefault(slug, new User());
        return new ResponseEntity<>(user,HttpStatus.valueOf(200));
    }



    @PostMapping("/chat/seen")
    public ResponseEntity<Map<String,Object>> getUserStatus(@RequestBody MessageDto message, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User  loggedUser = (User) request.getAttribute("user");
        message.setReceiver(loggedUser.getSlug());
        boolean updated = wholesaleUserService.updateSeenMessages(message);
        if(updated){
            result.put("message","Message successfully updated.");
            result.put("status", 201);
        }else{
            result.put("message","Something went wrong during updating all message seen.");
            result.put("status",  400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}