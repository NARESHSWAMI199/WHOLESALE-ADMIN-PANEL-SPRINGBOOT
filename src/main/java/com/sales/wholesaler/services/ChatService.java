package com.sales.wholesaler.services;

import com.sales.admin.services.RepoContainer;
import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
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


    public Map<String,List<Chat>> getAllChatBySenderAndReceiverKey(MessageDto message, HttpServletRequest request){
        List<Chat> chatList = chatRepository.getChatBySenderKeyOrReceiverKey(message.getSender(), message.getReceiver());
        Map<String,List<Chat>> formatedData = new TreeMap<>();

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

}
