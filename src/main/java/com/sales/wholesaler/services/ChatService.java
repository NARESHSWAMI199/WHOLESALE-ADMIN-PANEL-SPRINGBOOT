package com.sales.wholesaler.services;

import com.sales.admin.services.RepoContainer;
import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService extends RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Value("${chat.absolute}")
    String chatAbsolutePath;

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
        logger.info("Starting getAllChatBySenderAndReceiverKey method with messageDto : {} ",message);
        Map<String, List<Chat>> formatedData = new TreeMap<>();
        List<Chat> chatList = chatRepository.getChatBySenderKeyOrReceiverKey(message.getSender(), message.getReceiver());

        for (Chat chat : chatList) {
            if(chat.getReceiver().equals(message.getSender()) && chat.getIsSent().equals("F")) continue;
            String createAtDate = Utils.getStringDateOnly(chat.getCreatedAt());
            List<Chat> chats;
            if (formatedData.containsKey(createAtDate)) {
                chats = formatedData.get(createAtDate);
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
            formatedData.put(createAtDate, chats);
        }
        logger.info("Completed getAllChatBySenderAndReceiverKey method");
        return formatedData;
    }


    public Chat getParentMessageById(Long parentId,HttpServletRequest request){
        logger.info("Starting getAllChatBySenderAndReceiverKey method with parentId : {} ",parentId);
        Optional<Chat> chatOptional = chatRepository.findById(parentId);
        if (chatOptional.isPresent()){
            Chat chat = chatOptional.get();
            String images = chat.getImages();
            if (images != null) {
                String[] imagesList = images.split(",", -1);
                List<String> list = Arrays.stream(imagesList).map(name -> Utils.getHostUrl(request) + "/chat/images/" + chat.getSender() + "/" + chat.getReceiver() + "/" + name).collect(Collectors.toList());
                chat.setImagesUrls(list);
            }
            logger.info("Completed getAllChatBySenderAndReceiverKey method");
            return chat;
        }
        return  null;
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
        saveMessage(message,imagesNamesString); // Create operation
        logger.info("Completed addImagesList method");
        return message;
    }


    public boolean updateMessageToSent(Long id){
        return chatHbRepository.updateMessageToSent(id);
    }

}
