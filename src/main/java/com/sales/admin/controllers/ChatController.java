package com.sales.admin.controllers;

import com.sales.entities.Chat;
import com.sales.entities.Message;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.wholesaler.controller.WholesaleServiceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class ChatController extends WholesaleServiceContainer {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }



    @PostMapping("/chats/all")
    public ResponseEntity<List<Chat>> getALlUsers(@RequestBody Message message){
        List<Chat> allChats = chatService.getAllChatBySenderAndReceiverKey(message);
        return new ResponseEntity<>(allChats, HttpStatus.valueOf(200));
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        // Set the sender in the message
        message.setSenderKey(sender);
        return message;
    }


    @MessageMapping("/chat/private/{recipient}")
    public void sendPrivateMessage(@DestinationVariable String recipient, Message message, SimpMessageHeaderAccessor headerAccessor) {
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        if (recipient == null) throw new MyException("Please provide a valid recipient");
        System.err.println("Here : "+recipient);
        message.setSenderKey(sender);
        message.setReceiverKey(recipient);
        String slug = sender.split("_")[0];
        User user = wholesaleUserService.findUserBySlug(slug);
        chatService.saveMessage(user,message);
        /* you need to subscribe like  /user/{userId}/queue/private */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", message);
    }




    @MessageMapping("/chat/connect/{slug}")
    public void userConnected(@DestinationVariable String slug) {
        System.err.println("Connected");
        try{
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

    @MessageMapping("/chat/{slug}/userStatus")
    @SendTo("/topic/status")
    public User getUserStatus(@DestinationVariable String slug) {
        System.err.println("Status checking." + slug);
        return GlobalConstant.onlineUsers.get(slug);
    }
}