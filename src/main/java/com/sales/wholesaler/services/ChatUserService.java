package com.sales.wholesaler.services;

import com.sales.admin.services.RepoContainer;
import com.sales.entities.ChatUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUserService  extends RepoContainer {

    public List<User> getAllChatUsers(User loggedUser){
        List<User> userList = chatUserRepository.getChatUserByUserId(loggedUser.getId());
        for (User user : userList) {
            Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
            user.setChatNotification(unSeenChatsCount);
        }
        return userList;
    }


    public ChatUser addNewChatUser(User loggedUser ,String contactSlug){
        /* check chatUser exists or not */
        Integer userFound = chatRepository.isUserExistsInChatList(loggedUser.getSlug(),contactSlug);
        if(userFound > 0) return null;
        User contactUser = userRepository.findUserBySlug(contactSlug);
        if(contactUser == null) throw new MyException("Not a valid contact");
        ChatUser chatUser = ChatUser.builder()
                .userId(loggedUser.getId())
                .chatUser(contactUser)
                .build();
        return chatUserRepository.save(chatUser);
    }

}
