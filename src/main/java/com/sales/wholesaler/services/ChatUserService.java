package com.sales.wholesaler.services;

import com.sales.entities.ChatUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUserService  extends WholesaleRepoContainer {

    @Autowired
    protected BlockListService blockListService;

    public List<ChatUser> getAllChatUsers(User loggedUser, HttpServletRequest request){
        List<ChatUser> chatUserList = chatUserRepository.getChatUserByUserId(loggedUser.getId());
        List<User> userList = chatUserList.stream().map(ChatUser::getChatUser).toList();
        for (User user : userList) {
            Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
            String hostUrl = Utils.getHostUrl(request);
            user.setAvatar(hostUrl+ GlobalConstant.wholesalerImagePath +user.getSlug()+"/"+user.getAvatar());
            user.setChatNotification(unSeenChatsCount);
        }
        return chatUserList;
    }


    public ChatUser addNewChatUser(User sender ,User receiver,String status){
        /* check chatUser exists or not */
        ChatUser userFound = chatUserRepository.findByUserIdAndChatUser(sender.getId(),receiver);
        if(userFound != null) return userFound;

        ChatUser chatUser = ChatUser.builder()
                .userId(sender.getId())
                .status(status)
                .chatUser(receiver)
                .build();
        return chatUserRepository.save(chatUser);
    }



    public ChatUser addNewChatUser(User sender ,String receiverSlug,String status){

        User receiver = wholesaleUserRepository.findUserBySlug(receiverSlug);
        if(receiver == null) throw new MyException("Receiver not found.");
        /* check chatUser exists or not */
        ChatUser userFound = chatUserRepository.findByUserIdAndChatUser(sender.getId(),receiver);
        if(userFound == null) return null;

        ChatUser chatUser = ChatUser.builder()
                .userId(sender.getId())
                .chatUser(receiver)
                .status(status)
                .build();
        return chatUserRepository.save(chatUser);
    }


    public boolean updateAcceptStatus (Integer userId,String receiverSlug , String status) {
        System.err.println("user id ; "+userId + " receiverSlug : "+receiverSlug);
        User receiver = wholesaleUserRepository.findUserBySlug(receiverSlug);
        return  chatUserHbRepository.updateAcceptStatus(userId,receiver,status);
    }


}
