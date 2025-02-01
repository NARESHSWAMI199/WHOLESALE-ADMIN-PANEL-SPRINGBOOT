package com.sales.wholesaler.services;

import com.sales.admin.services.RepoContainer;
import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService extends RepoContainer {

    @Value("${chat.absolute}")
    String chatAbsolutePath;

    public Chat saveMessage(MessageDto message,String commaSeparatedImagesName) {
        Chat chat = Chat.builder()
//            .userId(loggedUser.getId())
            .sender(message.getSender())
            .receiver(message.getReceiver())
            .message(message.getMessage())
            .images(commaSeparatedImagesName)
            .isSenderDeleted("N")
            .isReceiverDeleted("N")
            .createdAt(message.getCreatedAt())
            .seen(false)
            .build();
        return chatRepository.save(chat);
    }


    public Map<String,List<Chat>> getAllChatBySenderAndReceiverKey(MessageDto message,HttpServletRequest request){
        Map<String,List<Chat>> formatedData = new TreeMap<>();
        List<Chat> chatList = chatRepository.getChatBySenderKeyOrReceiverKey(message.getSender(), message.getReceiver());

        for(Chat chat : chatList){
            String createAtDate = Utils.getStringDateOnly(chat.getCreatedAt());
            List<Chat> chats;
            if(formatedData.containsKey(createAtDate)){
                chats = formatedData.get(createAtDate);
            }else{
                chats = new ArrayList<>();
            }
            String images = chat.getImages();
            if(images != null) {
                String[] imagesList = images.split(",", -1);
                List<String> list = Arrays.stream(imagesList).map(name -> Utils.getHostUrl(request) + "/chat/images/" + chat.getSender() + "/" + chat.getReceiver() + "/" + name).collect(Collectors.toList());
                chat.setImagesUrls(list);
            }
            chats.add(chat);
            formatedData.put(createAtDate,chats);
        }

        return formatedData;

    }


    public List<String> saveAllImages(MessageDto messageDto, User loggedUser){
        List<String> imagesNames = new ArrayList<>();
        String folderPath = chatAbsolutePath + loggedUser.getSlug() +"_"+ messageDto.getReceiver() + File.separator;
        File directory = new File(folderPath);
        if(!directory.exists()) directory.mkdirs();
        try {
            for(MultipartFile multipartFile : messageDto.getImages()){
                String originalFilename = multipartFile.getOriginalFilename().replaceAll(" ","_");
                boolean validImage = Utils.isValidImage(originalFilename);
                if(!validImage) throw new MyException("Not valid images.");
                multipartFile.transferTo(new File( folderPath+ originalFilename));
                imagesNames.add(originalFilename);
            }
        }catch (Exception e){
            new File(folderPath).delete();
            throw new MyException(e.getMessage());
        }
        return imagesNames;
    }



    public MessageDto addImagesList (MessageDto message,HttpServletRequest request,List<String> allImagesName,User loggedUser,String recipient) {
        message.setImages(null);
        List<String> imageUrls = allImagesName.stream().map(name -> Utils.getHostUrl(request)+"/chat/images/" + loggedUser.getSlug() + "/" + message.getReceiver() + "/" + name).collect(Collectors.toList());
        message.setImagesUrls(imageUrls);
        message.setSender(loggedUser.getSlug());
        message.setReceiver(recipient);
        //message.setMessage(HtmlUtils.htmlEscape(message.getMessage()));
        String imagesNamesString = "";
        for(int i =0; i < allImagesName.size(); i++){
            imagesNamesString += allImagesName.get(i);
            if(i < (allImagesName.size()-1)){
                imagesNamesString +=',';
            }
        }
        // save the message in database
        saveMessage(message, imagesNamesString);
        return message;
    }

}
