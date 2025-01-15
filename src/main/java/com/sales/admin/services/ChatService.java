package com.sales.admin.services;

import com.sales.admin.controllers.ServiceContainer;
import com.sales.entities.Chat;
import com.sales.entities.Message;
import com.sales.entities.User;
import com.sales.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService extends RepoContainer {

    public Chat saveMessage(User loggedUser, Message message) {
        Chat chat = Chat.builder()
            .userId(loggedUser.getId())
            .senderKey(message.getSenderKey())
            .receiverKey(message.getReceiverKey())
            .createdAt(Utils.getCurrentMillis())
            .message(message.getMessage())
            .isDeleted("N")
            .build();
        return chatRepository.save(chat);
    }


    public List<Chat> getAllChatBySenderAndReceiverKey(Message message){
        return chatRepository.getChatBySenderKeyOrReceiverKey(message.getSenderKey(),message.getReceiverKey());
    }

}
