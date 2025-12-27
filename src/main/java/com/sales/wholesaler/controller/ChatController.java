package com.sales.wholesaler.controller;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.ConstantResponseKeys;
import com.sales.global.GlobalConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ChatController extends WholesaleServiceContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(com.sales.helpers.Logger safeLog, SimpMessagingTemplate messagingTemplate) {
        this.safeLog = safeLog;
        this.messagingTemplate = messagingTemplate;
    }


    /** @Note : Make sure all @MessageMappings 's prefix is /app/ */


    @PostMapping("/chats/all")
    public ResponseEntity<Map<String, List<Chat>>> getALlUsers(@RequestBody MessageDto message , HttpServletRequest request){
        safeLog.info(logger,"Fetching all users for message: {}", message);
        User loggedUser = (User) request.getAttribute("user");
        message.setSender(loggedUser.getSlug());
        Map<String, List<Chat>> formatedChatList = chatService.getAllChatBySenderAndReceiverKey(message,request);
        return new ResponseEntity<>(formatedChatList, HttpStatus.valueOf(200));
    }


    @GetMapping("/chats/message/{parentId}")
    public ResponseEntity<Chat> getParentChatMessageByParentId(@PathVariable Long parentId , HttpServletRequest request){
        safeLog.info(logger,"Fetching parent chat using parentId: {}", parentId);
        User loggedUser = (User) request.getAttribute("user");
        Chat parentChat = chatService.getParentMessageById(parentId,loggedUser,request);
        return new ResponseEntity<>(parentChat, HttpStatus.valueOf(200));
    }


    @PostMapping("/chats/parentId")
    public ResponseEntity<Integer> getParentChatMessageBySentTime(@RequestBody MessageDto message , HttpServletRequest request){
        safeLog.info(logger,"Fetching parent chat using createdAt: {} and sender : {} and receiver : {}", message.getCreatedAt(),message.getSender(),message.getReceiver());
        Integer parentMessageId = chatService.getParentMessageIdByCreatedAt(message,request);
        return new ResponseEntity<>(parentMessageId, HttpStatus.valueOf(200));
    }



    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public MessageDto sendMessage(MessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        safeLog.info(logger,"Sending public message: {}", message);
        // Get the sender's username
        String sender = (String) headerAccessor.getSessionAttributes().get("username");
        // Set the sender in the message
        message.setSender(sender);
        return message;
    }


    /** Here we are using two for receiving and sending chats
     * @sendPrivateMessage : Using for just only text because websocket are faster compare to api
     * @uploadImages : Using api for upload images because we are facing some issue share files or images with websocket
     * */

    @MessageMapping("/chat/private/{recipient}")
    public void sendPrivateMessage(@DestinationVariable String recipient, MessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        safeLog.info(logger,"Sending private message to recipient: {}", recipient);
        User loggedUser = (User) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("user");
        Chat sentMessage = chatService.sendMessage(message,loggedUser,message.getReceiver());
        if(sentMessage == null) return;
        /* you need to subscribe like  /user/{userId}/queue/private */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", sentMessage);
    }



    /** Upload images and other files with chat */
    @PostMapping("/chat/upload")
    public ResponseEntity<Map<String,Object>> uploadImages(@ModelAttribute MessageDto message ,HttpServletRequest request){
        safeLog.info(logger,"Uploading images for message: {}", message);
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        String recipient = message.getReceiver();
        User receiver = wholesaleUserService.findUserBySlug(recipient);
        if (receiver == null) throw new MyException("Please provide a valid recipient");

        boolean verified = chatService.verifyBeforeSend(loggedUser, recipient);
        if(!verified) return null;

        List<String> allImagesName = chatService.saveAllImages(message, loggedUser);
        if(allImagesName.size() == message.getImages().size()){
            result.put(ConstantResponseKeys.MESSAGE,"All images successfully sent.");
            result.put(ConstantResponseKeys.STATUS , 200);
        }else{
            result.put(ConstantResponseKeys.MESSAGE,"Something went wrong during save images.");
            result.put(ConstantResponseKeys.STATUS , 400);
        }

        /* ------------------------------- sending message and saving message ------------------------------- */
        message = chatService.addImagesList(message, request, allImagesName, loggedUser, recipient);
        chatService.updateMessageToSent(message.getId());
        message.setIsSent("S");
        /*
             You need to subscribe like /user/{userId}/queue/private
             Send a private message to the recipient
         */
        // going to update a message sent successfully
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private",message);

        /*!------------------ message block end ---------------------- */
        return new ResponseEntity<>(result,HttpStatus.valueOf(200));
    }




    @MessageMapping("/chat/connect/{slug}")
    public void userConnected(@DestinationVariable String slug, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        safeLog.info(logger,"User connected with slug: {}", slug);
        safeLog.info(logger,"Connected");
        try{
            String sender = Objects.requireNonNull(simpMessageHeaderAccessor.getSessionAttributes()).get("username").toString();
            safeLog.info(logger,"The sender is : {}",sender);
            User user = wholesaleUserService.findUserBySlug(slug);
            if(user == null) throw new MyException("Not valid user to connect for chat.");
            user.setOnline(true);
            wholesaleUserService.updateLastSeen(user);
            GlobalConstant.onlineUsers.put(slug, user);
        }catch (Exception e){
            safeLog.info(logger,e.getMessage());
        }

    }




    @MessageMapping("/chat/deactivate/{slug}")
    public void disconnectUser(@DestinationVariable String slug) {
        safeLog.info(logger,"User disconnected with slug: {}", slug);
        safeLog.info(logger,"Disconnected");
        try{
            User user = GlobalConstant.onlineUsers.get(slug);
            if(user == null) throw new MyException("Not valid user to connect for chat.");
            user.setOnline(false);
            wholesaleUserService.updateLastSeen(user);
            GlobalConstant.onlineUsers.put(slug, user);
        }catch (Exception e){
            safeLog.info(logger,e.getMessage());
        }
    }



    @MessageMapping("/chats/was-seen/{recipient}")
    public void isReceiverSeen(@DestinationVariable("recipient") String recipient,SimpMessageHeaderAccessor headerAccessor){
        User loggedUser = (User) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("user");
        User receiver = wholesaleUserService.findUserBySlug(recipient);
        if (recipient == null) throw new MyException("Please provide a valid recipient");
        safeLog.info(logger,"Seen Called.....");
        /* Check If you already blocked by receiver or not if blocked, then do nothing eat fivestar */
        boolean isYouBlockedByReceiver = blockListService.isSenderBlockedByReceiver(loggedUser,receiver);
        /* Check you blocked the receiver or not */
        boolean isYouBlockedReceiver = blockListService.isReceiverBlockedBySender(loggedUser,receiver);
        boolean seen = !isYouBlockedByReceiver && !isYouBlockedReceiver;
        safeLog.info(logger,"Message seen or not :  {} ",seen);
        /* you need to subscribe like  /user/{userId}/queue/private/chat/seen */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private/chat/seen",seen);
    }


    @MessageMapping("/chat/{slug}/userStatus")
    public void getUserStatus(@DestinationVariable String slug, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        safeLog.info(logger,"Checking user status for slug: {}", slug);
        String sender = (String) Objects.requireNonNull(simpMessageHeaderAccessor.getSessionAttributes()).get("username");
        /* you need to subscribe like  /user/{userId}/queue/private/status */
        messagingTemplate.convertAndSendToUser(sender, "/queue/private/status",GlobalConstant.onlineUsers.getOrDefault(slug,new User()));
    }



    @GetMapping("/chat/status/{slug}")
    public ResponseEntity<User> getUserStatus(@PathVariable String slug){
        safeLog.info(logger,"Getting user status for slug: {}", slug);
        User user = GlobalConstant.onlineUsers.getOrDefault(slug, new User());
        return new ResponseEntity<>(user,HttpStatus.valueOf(200));
    }



    @PostMapping("/chat/seen")
    public ResponseEntity<Map<String,Object>> getUserStatus(@RequestBody MessageDto message, HttpServletRequest request){
        safeLog.info(logger,"Updating seen status for message: {}", message);
        Map<String,Object> result = new HashMap<>();
        User  loggedUser = (User) request.getAttribute("user");
        message.setReceiver(loggedUser.getSlug());
        wholesaleUserService.updateSeenMessages(message);
        result.put(ConstantResponseKeys.MESSAGE,"Message successfully updated.");
        result.put(ConstantResponseKeys.STATUS, 200);
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }



    @Value("${chat.get}")
    String filePath;

    @GetMapping("/chat/images/{sender}/{receiver}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable(required = true) String filename
            , @PathVariable String sender,
    @PathVariable String receiver) throws Exception {
        safeLog.info(logger,"Fetching file: {} for sender: {} and receiver: {}", filename,sender, receiver);
        Path filePathObj = Paths.get(filePath);
        Path filePathDynamic = filePathObj.resolve(sender+"_"+receiver).normalize();
        Path path = filePathDynamic.resolve(filename).normalize();
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }


    @PostMapping("/chat/delete")
    public ResponseEntity<Map<String,Object>> deleteBySlug(@RequestBody MessageDto messageDto,HttpServletRequest request){
        safeLog.info(logger,"Deleting message: {}", messageDto);
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isDeleted = chatService.deleteMessage(loggedUser, messageDto);
        if(isDeleted > 0){
            result.put(ConstantResponseKeys.MESSAGE,"deleted successfully");
            result.put(ConstantResponseKeys.STATUS, 200);
        }else{
            result.put(ConstantResponseKeys.MESSAGE,"Something went wrong due to message deleted");
            result.put(ConstantResponseKeys.STATUS, 400);
        }
        if(messageDto.getIsDeleted().equals("B")){
            /* you need to subscribe like  /user/{userId}/queue/private/deleted */
            messagingTemplate.convertAndSendToUser(messageDto.getReceiver(), "/queue/private/deleted",messageDto);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));

    }













}