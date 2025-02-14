package com.sales.wholesaler.controller;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ChatController extends WholesaleServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    /** @Note : Make sure all @MessageMappings 's prefix is /app/ */


    @PostMapping("/chats/all")
    public ResponseEntity<Map<String, List<Chat>>> getALlUsers(@RequestBody MessageDto message , HttpServletRequest request){
        logger.info("Fetching all users for message: {}", message);
        User loggedUser = (User) request.getAttribute("user");
        message.setSender(loggedUser.getSlug());
        Map<String, List<Chat>> formatedChatList = chatService.getAllChatBySenderAndReceiverKey(message,request);
        return new ResponseEntity<>(formatedChatList, HttpStatus.valueOf(200));
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public MessageDto sendMessage(MessageDto message, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("Sending public message: {}", message);
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
        logger.info("Sending private message to recipient: {}", recipient);
        User sender = (User) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("user");
        message.setSender(sender.getSlug());
        message.setReceiver(recipient);
        //message.setMessage(HtmlUtils.htmlEscape(message.getMessage()));
        Chat savedMessage = chatService.saveMessage(message, null);

        User receiver = wholesaleUserService.findUserBySlug(recipient);
        if (receiver == null) throw new MyException("Please provide a valid recipient");

        /* Added new user in to sender's chat list*/
        chatUserService.addNewChatUser(sender, receiver,"S");

        /* Check you are blocked by receiver or not */
        boolean isBlocked = blockListService.isUserExistsInBlockList(sender,receiver);
        if (isBlocked) return; //  If isBlocked == true that's mean. receiver already blocked you

        /* Added sender in to the recipients chat list*/
        chatUserService.addNewChatUser(receiver, sender,"A");

        if (recipient == null) throw new MyException("Please provide a valid recipient");
        /* you need to subscribe like  /user/{userId}/queue/private */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", savedMessage != null ? savedMessage : message);
    }



    /** Upload images and other files with chat */
    @PostMapping("/chat/upload")
    public ResponseEntity<Map<String,Object>> uploadImages(@ModelAttribute MessageDto message ,HttpServletRequest request){
        logger.info("Uploading images for message: {}", message);
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        String recipient = message.getReceiver();
        User receiver = wholesaleUserService.findUserBySlug(recipient);
        if (receiver == null) throw new MyException("Please provide a valid recipient");

        /* Added new user in to sender's chat list*/
        chatUserService.addNewChatUser(loggedUser, receiver,"A");

        /* Check If you already blocked by receiver or not if blocked then do nothing just eat fivestar */
        boolean isBlocked = blockListService.isUserExistsInBlockList(loggedUser,receiver);
        if (isBlocked) {
            return new ResponseEntity<>(new HashMap<>(),HttpStatus.OK);
        }

        /* Added sender in to the recipients chat list*/
        chatUserService.addNewChatUser(receiver, loggedUser,"S");


        List<String> allImagesName = chatService.saveAllImages(message, loggedUser);
        if(allImagesName.size() == message.getImages().size()){
            result.put("message","All images successfully sent.");
            result.put("status" , 200);
        }else{
            result.put("message","Something went wrong during save images.");
            result.put("status" , 400);
        }

        /** ------------------------------- sending message and saving message ------------------------------- */
        message = chatService.addImagesList(message, request, allImagesName, loggedUser, recipient);
        /*
             You need to subscribe like  /user/{userId}/queue/private
             Send a private message to recipient
         */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private",message);

        /**!------------------ message block end ---------------------- */
        return new ResponseEntity<>(result,HttpStatus.valueOf(200));
    }




    @MessageMapping("/chat/connect/{slug}")
    public void userConnected(@DestinationVariable String slug, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        logger.info("User connected with slug: {}", slug);
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
        logger.info("User disconnected with slug: {}", slug);
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
        logger.info("Message seen by recipient: {}", recipient);
        if (recipient == null) throw new MyException("Please provide a valid recipient");
        System.err.println("Seen Called.....");
        /* you need to subscribe like  /user/{userId}/queue/private/chat/seen */
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private/chat/seen",true);
    }


    @MessageMapping("/chat/{slug}/userStatus")
    public void getUserStatus(@DestinationVariable String slug, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        logger.info("Checking user status for slug: {}", slug);
        System.err.println("Status checking." + slug);
        String sender = (String) simpMessageHeaderAccessor.getSessionAttributes().get("username");
        /* you need to subscribe like  /user/{userId}/queue/private/status */
        messagingTemplate.convertAndSendToUser(sender, "/queue/private/status",GlobalConstant.onlineUsers.getOrDefault(slug,new User()));
    }



    @GetMapping("/chat/status/{slug}")
    public ResponseEntity<User> getUserStatus(@PathVariable String slug){
        logger.info("Getting user status for slug: {}", slug);
        User user = GlobalConstant.onlineUsers.getOrDefault(slug, new User());
        return new ResponseEntity<>(user,HttpStatus.valueOf(200));
    }



    @PostMapping("/chat/seen")
    public ResponseEntity<Map<String,Object>> getUserStatus(@RequestBody MessageDto message, HttpServletRequest request){
        logger.info("Updating seen status for message: {}", message);
        Map<String,Object> result = new HashMap<>();
        User  loggedUser = (User) request.getAttribute("user");
        message.setReceiver(loggedUser.getSlug());
        boolean updated = wholesaleUserService.updateSeenMessages(message);
        if(updated){
            result.put("message","Message successfully updated.");
            result.put("status", 200);
        }else{
            result.put("message","Something went wrong during updating all message seen.");
            result.put("status",  400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }



    @Value("${chat.get}")
    String filePath;

    @GetMapping("/chat/images/{sender}/{receiver}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable(required = true) String filename
            , @PathVariable String sender,
    @PathVariable String receiver) throws Exception {
        logger.info("Fetching file: {} for sender: {} and receiver: {}", filename, sender, receiver);
        Path path = Paths.get(filePath +sender+"_"+receiver+File.separator+ filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }


    @PostMapping("/chat/delete")
    public ResponseEntity<Map<String,Object>> deleteBySlug(@RequestBody MessageDto messageDto,HttpServletRequest request){
        logger.info("Deleting message: {}", messageDto);
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        int isDeleted = wholesaleUserService.deleteMessage(loggedUser, messageDto);
        if(isDeleted > 0){
            result.put("message","deleted successfully");
            result.put("status", 200);
        }else{
            result.put("message","Something went wrong due to message deleted");
            result.put("status", 400);
        }
        if(messageDto.getIsDeleted().equals("B")){
            /* you need to subscribe like  /user/{userId}/queue/private/deleted */
            messagingTemplate.convertAndSendToUser(messageDto.getReceiver(), "/queue/private/deleted",messageDto);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));

    }













}