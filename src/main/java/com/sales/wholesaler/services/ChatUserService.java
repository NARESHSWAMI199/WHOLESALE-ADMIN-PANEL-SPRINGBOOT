package com.sales.wholesaler.services;

import com.sales.entities.ChatUser;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.exceptions.NotFoundException;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatUserService extends WholesaleRepoContainer {

    private final com.sales.helpers.Logger log;
     private static final Logger logger = LoggerFactory.getLogger(ChatUserService.class);

    @Autowired
    protected BlockListService blockListService;

    public List<User> getAllChatUsers(User loggedUser, HttpServletRequest request) {
        log.info(logger,"Starting getAllChatUsers method and the user id : {}",loggedUser.getId());
        List<ChatUser> chatUserList = chatUserRepository.getChatUserByUserId(loggedUser.getId()).stream().filter(chatUser -> chatUser.getChatUser() !=null).toList();
        List<User> userList = chatUserList.stream().map(ChatUser::getChatUser).toList();

        for (User user : userList) {
            if (user != null) {
                Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
                String hostUrl = Utils.getHostUrl(request);
                user.setAvatarUrl(hostUrl + GlobalConstant.wholesalerImagePath + user.getSlug() + GlobalConstant.PATH_SEPARATOR + user.getAvatar());
                user.setChatNotification(unSeenChatsCount);
            }
        }
        log.info(logger,"Completed getAllChatUsers method");
        return userList;
    }

    public ChatUser addNewChatUser(User sender, User receiver, String status) {
        log.info(logger,"Starting addNewChatUser method with User receiver there the sender is : {} and the receiver is : {} ",sender.getId(),receiver.getId());
        ChatUser userFound = chatUserRepository.findByUserIdAndChatUser(sender.getId(), receiver);
        if (userFound != null) {
            log.info(logger,"ChatUser already exists, returning existing user checking in addNewChatUser method");
            return userFound;
        }

        ChatUser chatUser = ChatUser.builder()
            .userId(sender.getId())
            .senderAcceptStatus(status)
            .chatUser(receiver)
            .build();
        ChatUser savedChatUser = chatUserRepository.save(chatUser); // Create operation
        log.info(logger,"Completed addNewChatUser method with User receiver");
        return savedChatUser;
    }

    public ChatUser addNewChatUser(User sender, String receiverSlug, String status) {
        log.info(logger,"Starting addNewChatUser method with receiverSlug");
        User receiver = wholesaleUserRepository.findUserBySlug(receiverSlug);
        if (receiver == null) {
            logger.error("Receiver not found");
            throw new MyException("Receiver not found.");
        }
        ChatUser userFound = chatUserRepository.findByUserIdAndChatUser(sender.getId(), receiver);
        if (userFound != null) {
            log.info(logger,"ChatUser already exists, returning existing user");
            return userFound;
        }

        ChatUser chatUser = ChatUser.builder()
            .userId(sender.getId())
            .chatUser(receiver)
            .senderAcceptStatus(status)
            .build();
        ChatUser savedChatUser = chatUserRepository.save(chatUser); // Create operation
        log.info(logger,"Completed addNewChatUser method with receiverSlug");
        return savedChatUser;
    }

    public boolean updateAcceptStatus(Integer userId, String receiverSlug, String status) {
        log.info(logger,"Starting updateAcceptStatus method");
        User receiver = wholesaleUserRepository.findUserBySlug(receiverSlug);
        boolean isUpdated = chatUserHbRepository.updateAcceptStatus(userId, receiver, status); // Update operation
        log.info(logger,"Completed updateAcceptStatus method");
        return isUpdated;
    }



    public String isChatRequestAcceptedByLoggedUser(User loggedUser,User receiver) {
        log.info(logger,"Starting isChatRequestAccepted method with userId : {} and chatUserId : {} ",loggedUser.getId(),receiver.getId());
        ChatUser chatUser = chatUserRepository.findByUserIdAndChatUser(loggedUser.getId(), receiver);
        if(chatUser == null) throw new NotFoundException("User not found in your chat users list.");
        log.info(logger,"Completed isChatRequestAccepted method");
        return chatUser.getSenderAcceptStatus();
    }


    public String isChatRequestAcceptedByLoggedUser(User loggedUser,String receiverSlug) {
        log.info(logger,"Starting isChatRequestAccepted method with userId : {} and chatUser : {} ",loggedUser.getId(),receiverSlug);
        User receiver = wholesaleUserRepository.findUserBySlug(receiverSlug);
        if(receiver == null) throw new NotFoundException("Receiver not found.");
        ChatUser chatUser = chatUserRepository.findByUserIdAndChatUser(loggedUser.getId(),receiver);
        if(chatUser == null) throw new NotFoundException("Receiver not found.");
        log.info(logger,"Completed isChatRequestAcceptedByLoggedUser method");
        return chatUser.getSenderAcceptStatus();
    }


    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public int removeChatUser(User loggedUser,String chatUserSlug,Boolean deleteChats) {
        log.info(logger,"Going to remove contact from contact list with loggedUser  {} : and chatUserSlug {} ",loggedUser,chatUserSlug);
        User contactUser = wholesaleUserRepository.findUserBySlug(chatUserSlug);
        if(contactUser == null) throw new NotFoundException("No contact user found to delete.");
        Integer deleted = chatUserRepository.deleteChatUserFromChatList(loggedUser.getId(), contactUser);
        if (deleted > 0 && deleteChats) {
            chatHbRepository.deleteChats(loggedUser.getSlug(),chatUserSlug);
        }
        return deleted;
    }


}
