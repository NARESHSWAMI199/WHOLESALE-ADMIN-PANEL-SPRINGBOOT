package com.sales.admin.services;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class ChatService extends RepoContainer {

    @Value("${chat.absolute}")
    String chatAbsolutePath;

    public Chat saveMessage(MessageDto message) {
        Chat chat = Chat.builder()
//            .userId(loggedUser.getId())
            .sender(message.getSender())
            .receiver(message.getReceiver())
            .createdAt(Utils.getCurrentMillis())
            .message(message.getMessage())
            .isDeleted("N")
            .seen(false)
            .build();
        return chatRepository.save(chat);
    }


    public Map<String,List<Chat>> getAllChatBySenderAndReceiverKey(MessageDto message){
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
            chats.add(chat);
            formatedData.put(createAtDate,chats);
        }

        return formatedData;

    }


    public boolean saveAllImages(MessageDto messageDto, User loggedUser){
        boolean result = false;
        String folderPath = chatAbsolutePath + loggedUser.getSlug() +"_"+ messageDto.getReceiver() + File.separator;
        try {
            for(MultipartFile multipartFile : messageDto.getImages()){
                String originalFilename = multipartFile.getOriginalFilename();
                boolean validImage = Utils.isValidImage(originalFilename);
                if(!validImage) throw new MyException("Not valid images.");
                multipartFile.transferTo(new File( folderPath+ originalFilename));
                result = true;
            }
        }catch (Exception e){
            new File(folderPath).delete();
            throw new MyException(e.getMessage());
        }
        return result;
    }

}
