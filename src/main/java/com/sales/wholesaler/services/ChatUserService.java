package com.sales.wholesaler.services;

import com.sales.entities.ChatUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUserService  extends WholesaleRepoContainer {

    @Autowired
    protected BlockListService blockListService;

    public List<User> getAllChatUsers(User loggedUser){
        List<User> userList = chatUserRepository.getChatUserByUserId(loggedUser.getId());
        for (User user : userList) {
            Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
            user.setChatNotification(unSeenChatsCount);
        }
        return userList;
    }


    public ChatUser addNewChatUser(User loggedUser ,String chatUserSlug){

        User contactUser = wholesaleUserRepository.findUserBySlug(chatUserSlug);
        if(contactUser == null) throw new MyException("Not a valid contact");

        /* check chatUser exists or not */
        Integer userFound = chatRepository.isUserExistsInChatList(loggedUser.getSlug(),chatUserSlug);
        if(userFound > 0) return null;

        ChatUser chatUser = ChatUser.builder()
                .userId(loggedUser.getId())
                .chatUser(contactUser)
                .build();
        return chatUserRepository.save(chatUser);
    }


}
