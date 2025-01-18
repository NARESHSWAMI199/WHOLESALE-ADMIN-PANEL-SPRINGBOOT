package com.sales.admin.services;

import com.sales.dto.MessageDto;
import com.sales.entities.Chat;
import com.sales.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public List<Chat> getAllChatBySenderAndReceiverKey(MessageDto message){
        return chatRepository.getChatBySenderKeyOrReceiverKey(message.getSender(),message.getReceiver());
    }

}
