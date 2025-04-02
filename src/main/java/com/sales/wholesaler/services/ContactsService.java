package com.sales.wholesaler.services;

import com.sales.admin.services.RepoContainer;
import com.sales.entities.Contact;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import com.sales.global.GlobalConstant;
import com.sales.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ContactsService extends RepoContainer {

    private static final Logger logger = LoggerFactory.getLogger(ContactsService.class);

    public List<User> getAllContactsByUserId(User loggedUser, HttpServletRequest request) {
        logger.info("Starting getAllContactsByUserId method");
        List<User> userList = contactRepository.getContactByUserId(loggedUser.getId()).stream().filter(Objects::nonNull).toList();
        for (User user : userList) {
            Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
            String hostUrl = Utils.getHostUrl(request);
            user.setAvatar(hostUrl + GlobalConstant.wholesalerImagePath + user.getSlug() + "/" + user.getAvatar());
            user.setChatNotification(unSeenChatsCount);
        }
        logger.info("Completed getAllContactsByUserId method");
        return userList;
    }

    public Contact addNewContact(User loggedUser, String contactSlug) {
        logger.info("Starting addNewContact method");
        Integer userFound = chatRepository.isUserExistsInChatList(loggedUser.getSlug(), contactSlug);
        if (userFound > 0) {
            logger.info("User already exists in chat list, returning null");
            return null;
        }
        User contactUser = userRepository.findUserBySlug(contactSlug);
        if (contactUser == null) {
            logger.error("Not a valid contact");
            throw new MyException("Not a valid contact");
        }
        Contact contacts = Contact.builder()
            .userId(loggedUser.getId())
            .contact(contactUser)
            .build();
        Contact savedContact = contactRepository.save(contacts); // Create operation
        logger.info("Completed addNewContact method");
        return savedContact;
    }

}
