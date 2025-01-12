package com.sales.admin.controllers;

import com.sales.entities.Greeting1;
import com.sales.entities.Message;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

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



 /** TODO : private chat */
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

}