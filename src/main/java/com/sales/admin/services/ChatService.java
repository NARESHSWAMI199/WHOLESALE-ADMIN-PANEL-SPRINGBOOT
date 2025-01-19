package com.sales.admin.services;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService extends RepoContainer {

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

}
