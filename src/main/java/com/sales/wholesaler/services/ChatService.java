package com.sales.wholesaler.services;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService extends WholesaleRepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Value("${chat.absolute}")
    private String chatAbsolutePath;

    @Autowired
    private ChatUserService chatUserService;


    @Autowired
    BlockListService blockListService;



    public Chat sendMessage(MessageDto message, User loggedUser, String recipient){
        message.setSender(loggedUser.getSlug());
        message.setReceiver(recipient);
        //message.setMessage(HtmlUtils.htmlEscape(message.getMessage()));
        Chat savedMessage = saveMessage(message, null);

        // verifying user sender or receiver blocked or not.
        boolean readyToSend = verifyBeforeSend(loggedUser, recipient);
        if(!readyToSend) return null;

        // Going to update a message
        updateMessageToSent(savedMessage.getId());
        savedMessage.setIsSent("S");
        return savedMessage;
    }


    public boolean verifyBeforeSend(User loggedUser,String recipient) {
        if (recipient == null) throw new MyException("Please provide a valid recipient");

        User receiver = wholesaleUserRepository.findUserBySlug(recipient);
        if (receiver == null) throw new MyException("Please provide a valid recipient");
        /* Added new user in to sender's chat list →
        sender = loggedUser | receiver = who receives this message | status = sender Accepted
        or not default it's A  */
        chatUserService.addNewChatUser(loggedUser, receiver,"A");

        /* Added sender in to the recipient chat list →
        sender = loggedUser | receiver = who receives this message | status =s
        receiver accepted or not default it's P */
        chatUserService.addNewChatUser(receiver, loggedUser,"P");

        /* Check you are blocked by receiver or not */
        boolean isYouBlockedByReceiver = blockListService.isSenderBlockedByReceiver(loggedUser,receiver);
        if (isYouBlockedByReceiver) {
            logger.info("Receiver blocked you ? : {} ", true);
            return false; //  If isBlocked == true, that's mean. Receiver already blocked you
        }

        /* Check you blocked the receiver or not */
        boolean isYouBlockedReceiver = blockListService.isReceiverBlockedBySender(loggedUser,receiver);
        if(isYouBlockedReceiver){
            logger.info("You blocked receiver ? : {} ", true);
        }
        return !isYouBlockedReceiver; //  If isYouBlockedReceiver == true, that's mean.
    }



    public Chat saveMessage(MessageDto message,String commaSeparatedImagesName) {
        logger.info("Starting saveMessage method");
        Chat chat = Chat.builder()
            .parentId(message.getParentId())
//            .userId(loggedUser.getId())
            .sender(message.getSender())
            .receiver(message.getReceiver())
            .message(message.getMessage())
            .images(commaSeparatedImagesName)
            .isSenderDeleted("N")
            .isReceiverDeleted("N")
            .createdAt(message.getCreatedAt())
            .isSent("F")
            .seen(false)
            .build();
        Chat savedChat = chatRepository.save(chat); // Create operation
        logger.info("Completed saveMessage method");
        return savedChat;
    }

    public Map<String, List<Chat>> getAllChatBySenderAndReceiverKey(MessageDto message,HttpServletRequest request) {
        // sender is loggedUser
        logger.info("Starting getAllChatBySenderAndReceiverKey method with messageDto : {} ",message);
        Map<String, List<Chat>> formattedData = new TreeMap<>();
        List<Chat> chatList = chatRepository.getChatBySenderKeyOrReceiverKey(message.getSender(), message.getReceiver());
        for (Chat chat : chatList) {
            // checking message sent or not.
            if(chat.getReceiver().equals(message.getSender()) && chat.getIsSent().equals("F")) continue;


            if(chat.getReceiver().equals(message.getSender())){
                // skipping deleted messages.
                if(chat.getIsReceiverDeleted().equals("Y")) continue;
                // hiding deleted messages.
                if(chat.getIsReceiverDeleted().equals("H")) {
                    chat.setMessage("Message was deleted.");
                    chat.setImages(null);
                }
            } if(chat.getSender().equals(message.getSender())){
                // skipping deleted messages.
                if(chat.getIsSenderDeleted().equals("Y")) continue;
                // hiding deleted messages.
                if(chat.getIsSenderDeleted().equals("H")) {
                    chat.setMessage("Message was deleted.");
                    chat.setImages(null);
                }
            }

            String createAtDate = Utils.getStringDateOnly(chat.getCreatedAt());
            List<Chat> chats;
            if (formattedData.containsKey(createAtDate)) {
                chats = formattedData.get(createAtDate);
            } else {
                chats = new ArrayList<>();
            }
            String images = chat.getImages();
            if (images != null) {
                String[] imagesList = images.split(",", -1);
                List<String> list = Arrays.stream(imagesList).map(name -> Utils.getHostUrl(request) + "/chat/images/" + chat.getSender() + "/" + chat.getReceiver() + "/" + name).collect(Collectors.toList());
                chat.setImagesUrls(list);
            }
            chats.add(chat);
            formattedData.put(createAtDate, chats);
        }
        logger.info("Completed getAllChatBySenderAndReceiverKey method");
        return formattedData;
    }


    public Chat getParentMessageById(Long parentId,User loggedUser,HttpServletRequest request){
        logger.info("Starting getParentMessageById method with parentId : {} ",parentId);
        Optional<Chat> chatOptional = chatRepository.findById(parentId);
        if (chatOptional.isPresent()){
            Chat chat = chatOptional.get();
            if(chat.getReceiver().equals(loggedUser.getSlug())){
                // hiding deleted messages.
                if(chat.getIsReceiverDeleted().equals("Y")  || chat.getIsReceiverDeleted().equals("H")) chat.setMessage("Message was deleted.");;
            } if(chat.getSender().equals(loggedUser.getSlug())){
                // hiding deleted messages.
                if(chat.getIsSenderDeleted().equals("Y") || chat.getIsSenderDeleted().equals("H")) chat.setMessage("Message was deleted.");
            }
            String images = chat.getImages();
            if (images != null) {
                String[] imagesList = images.split(",", -1);
                List<String> list = Arrays.stream(imagesList).map(name -> Utils.getHostUrl(request) + "/chat/images/" + chat.getSender() + "/" + chat.getReceiver() + "/" + name).collect(Collectors.toList());
                chat.setImagesUrls(list);
            }
            logger.info("Completed getParentMessageById method");
            return chat;
        }
        return  null;
    }



    public Integer getParentMessageIdByCreatedAt(MessageDto messageDto, HttpServletRequest request){
        logger.info("Starting getParentMessageIdByCreatedAt method with messageDto : {}",messageDto);
        Integer parentMessageId = chatRepository.getParentMessageIdByCreateAt(messageDto.getSender(),messageDto.getReceiver(),messageDto.getCreatedAt());
        logger.info("Completed getParentMessageIdByCreatedAt method");
        return parentMessageId;
    }



    public List<String> saveAllImages(MessageDto messageDto, User loggedUser) {
        logger.info("Starting saveAllImages method");
        List<String> imagesNames = new ArrayList<>();
        String folderPath = chatAbsolutePath + loggedUser.getSlug() + "_" + messageDto.getReceiver() + File.separator;
        File directory = new File(folderPath);
        if (!directory.exists()) directory.mkdirs();
        try {
            for (MultipartFile multipartFile : messageDto.getImages()) {
                String originalFilename = multipartFile.getOriginalFilename().replaceAll(" ", "_");
                boolean validImage = Utils.isValidImage(originalFilename);
                if (!validImage) throw new MyException("Not valid images.");
                multipartFile.transferTo(new File(folderPath + originalFilename));
                imagesNames.add(originalFilename);
            }
        } catch (Exception e) {
            new File(folderPath).delete();
            throw new MyException(e.getMessage());
        }
        logger.info("Completed saveAllImages method");
        return imagesNames;
    }

    public MessageDto addImagesList(MessageDto message, HttpServletRequest request, List<String> allImagesName, User loggedUser, String recipient) {
        logger.info("Starting addImagesList method");
        message.setImages(null);
        List<String> imageUrls = allImagesName.stream().map(name -> Utils.getHostUrl(request) + "/chat/images/" + loggedUser.getSlug() + "/" + message.getReceiver() + "/" + name).collect(Collectors.toList());
        message.setImagesUrls(imageUrls);
        message.setSender(loggedUser.getSlug());
        message.setReceiver(recipient);
        String imagesNamesString = String.join(",", allImagesName);
        Chat savedMessage = saveMessage(message,imagesNamesString); // Create operation
        message.setId(savedMessage.getId());
        logger.info("Completed addImagesList method");
        return message;
    }


    public boolean updateMessageToSent(Long id){
        return chatHbRepository.updateMessageToSent(id);
    }


    public int deleteMessage(User loggedUser,MessageDto messageDto){
        logger.info("Starting deleteMessage method with loggedUser: {}, messageDto: {}", loggedUser, messageDto);
        switch (messageDto.getIsDeleted()){
            case "S": // delete sender's message
                if(!messageDto.getSender().equals(loggedUser.getSlug())) return  0;
                messageDto.setIsSenderDeleted("H");
                messageDto.setIsReceiverDeleted(null);
                break;
            case "SY" : // Force delete sender's message
                if(!messageDto.getSender().equals(loggedUser.getSlug())) return  0;
                messageDto.setIsSenderDeleted("Y");
                messageDto.setIsReceiverDeleted(null);
                break;
            case "R": // delete receiver's message
                if(!messageDto.getReceiver().equals(loggedUser.getSlug())) return 0;
                messageDto.setIsSenderDeleted(null);
                messageDto.setIsReceiverDeleted("H");
                break;
            case "RY": // Force delete receiver's message
                if(!messageDto.getReceiver().equals(loggedUser.getSlug())) return 0;
                messageDto.setIsSenderDeleted(null);
                messageDto.setIsReceiverDeleted("Y");
                break;
            case "B": // Delete it from both sides
                if(!messageDto.getReceiver().equals(loggedUser.getSlug()) && !messageDto.getSender().equals(loggedUser.getSlug())) return 0;
                messageDto.setIsSenderDeleted("H");
                messageDto.setIsReceiverDeleted("H");
                break;
            default:
                return 0;
        }
        int deleteCount = chatHbRepository.deleteChat(messageDto); // Update operation
        logger.info("Completed deleteMessage method");
        return deleteCount;
    }

}
