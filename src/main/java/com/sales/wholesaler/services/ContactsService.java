package com.sales.wholesaler.services;

import com.sales.entities.Contact;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContactsService extends WholesaleRepoContainer {

    
    private static final Logger logger = LoggerFactory.getLogger(ContactsService.class);

    @Autowired
    private BlockListService blockListService;

    public List<User> getAllContactsByUserId(User loggedUser, HttpServletRequest request) {
        logger.debug("Starting getAllContactsByUserId method");
        List<User> userList = contactRepository.getContactByUserId(loggedUser.getId()).stream().filter(Objects::nonNull).toList();
        for (User user : userList) {
            Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
            String hostUrl = Utils.getHostUrl(request);
            user.setAvatarUrl(hostUrl + GlobalConstant.wholesalerImagePath + user.getSlug() + GlobalConstant.PATH_SEPARATOR + user.getAvatar());
            user.setChatNotification(unSeenChatsCount);
            //user.setBlocked(blockListService.isReceiverBlockedBySender(loggedUser,user));
            // Verifying the contact user existing in chats and sender chat request accepted or not.
            //user.setAccepted(chatUserRepository.getSenderAcceptStatus(loggedUser.getId(),user));
        }
        logger.debug("Completed getAllContactsByUserId method");
        return userList;
    }

    public Contact addNewContact(User loggedUser, String contactSlug) {
        logger.debug("Starting addNewContact method loggedUser slug : {} and contactSlug : {}",loggedUser.getSlug(),contactSlug);
        // TODO : check if user already in contact list not chat list
//        Integer userFound = chatRepository.isUserExistsInChatList(loggedUser.getSlug(), contactSlug);
//        if (userFound > 0) {
//            logger.debug("User already exists in chat list, returning null");
//            return null;
//        }
        User contactUser = wholesaleUserRepository.findUserBySlug(contactSlug);
        if (contactUser == null) {
            logger.error("Not a valid contact");
            throw new MyException("Not a valid contact");
        }
        Contact contacts = Contact.builder()
            .userId(loggedUser.getId())
            .contactUser(contactUser)
            .build();
        Contact savedContact = contactRepository.save(contacts); // Create operation
        logger.debug("Completed addNewContact method");
        return savedContact;
    }

    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public int removeContact(User loggedUser,String contactUserSlug,Boolean deleteChats) {
        logger.debug("Going to remove contact from contact list with loggedUser  {} : and contactUserSlug {} ",loggedUser,contactUserSlug);
        User contactUser = wholesaleUserRepository.findUserBySlug(contactUserSlug);
        if(contactUser == null) throw new NotFoundException("No contact user found to delete.");
        Integer deleted = contactRepository.deleteContactUserFromContact(loggedUser.getId(), contactUser);
        if (deleted > 0 && deleteChats) {
            chatHbRepository.deleteChats(loggedUser.getSlug(),contactUserSlug);
        }
        return deleted;
    }

}
