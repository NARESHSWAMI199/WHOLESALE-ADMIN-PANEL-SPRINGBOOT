package com.sales.admin.controllers;

import com.sales.dto.Message;
import com.sales.entities.Chat;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        message.setSender(sender);
        return message;
    }


    @MessageMapping("/chat/private/{recipient}")
    public void sendPrivateMessage(@DestinationVariable String recipient, Message message, SimpMessageHeaderAccessor headerAccessor) {
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        System.err.println("Here : "+recipient);
        message.setSender(sender);
        message.setReceiver(recipient);
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

}