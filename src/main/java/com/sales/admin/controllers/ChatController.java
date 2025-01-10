package com.sales.admin.controllers;

import com.sales.entities.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> userSessions = new HashMap<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public HelloMessage sendMessage(HelloMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");

        // Set the sender in the message
        message.setSender(sender);

        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public HelloMessage addUser(HelloMessage message, SimpMessageHeaderAccessor headerAccessor) {
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
    public HelloMessage removeUser(HelloMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");

        // Remove the session ID from the map
        userSessions.remove(sender);

        message.setType("LEAVE");
        message.setSender(sender);

        return message;
    }

    // ... other message handling methods (e.g., private messages) ...
}