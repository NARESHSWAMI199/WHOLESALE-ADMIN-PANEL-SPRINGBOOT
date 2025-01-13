package com.sales.admin.controllers;

import com.sales.entities.Message;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.wholesaler.controller.WholesaleServiceContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController extends WholesaleServiceContainer {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> userSessions = new HashMap<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
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

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");

        // Store the session ID associated with the username
        String sessionId = headerAccessor.getSessionId();
        userSessions.put(sender, sessionId);

        message.setType("JOIN");
        message.setSender(sender);

        return message;
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public Message removeUser(Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");

        // Remove the session ID from the map
        userSessions.remove(sender);

        message.setType("LEAVE");
        message.setSender(sender);

        return message;
    }

    // ... other message handling methods (e.g., private messages) ...


    /**
     * TODO : private chat
     */
    private final Map<String, String> privateChatSessions = new ConcurrentHashMap<>();

    private String getChatKey(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }


    @MessageMapping("/chat/private/{recipient}")
    public void sendPrivateMessage(@DestinationVariable String recipient, Message message, SimpMessageHeaderAccessor headerAccessor) {
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        if (recipient == null) throw new MyException("Please provide a valid recipient");
        message.setSender(sender);
        /* you need to subscribe like  /user/{userId}/queue/private */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", message);
    }




    @MessageMapping("/chat/connect/{slug}")
    public void userConnected(@DestinationVariable String slug) {
        System.err.println("Connected");
        User user = wholesaleUserService.findUserBySlug(slug);
        user.setOnline(true);
        wholesaleUserService.updateLastSeen(user);
        GlobalConstant.onlineUsers.put(slug, user);
    }




    @MessageMapping("/chat/deactivate/{slug}")
    public void deactiveUser(@DestinationVariable String slug) {
        System.err.println("Disconnected");
        User user = GlobalConstant.onlineUsers.get(slug);
        if (user != null) {
            user.setOnline(false);
            wholesaleUserService.updateLastSeen(user);
            GlobalConstant.onlineUsers.put(slug, user);
        }
    }

    @MessageMapping("/chat/{slug}/userStatus")
    @SendTo("/topic/status")
    public User getUserStatus(@DestinationVariable String slug) {
        System.err.println("Status checking." + slug);
        return GlobalConstant.onlineUsers.get(slug);
    }
}