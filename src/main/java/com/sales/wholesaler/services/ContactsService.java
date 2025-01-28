package com.sales.wholesaler.services;



import com.sales.admin.services.RepoContainer;
import com.sales.entities.Contact;
import com.sales.entities.User;
import com.sales.exceptions.MyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactsService  extends RepoContainer {

    public List<User> getAllContactsByUserId(User loggedUser){
        List<User> userList = contactRepository.getContactByUserId(loggedUser.getId());
        for (User user : userList) {
            Integer unSeenChatsCount = chatRepository.getUnSeenChatsCount(user.getSlug(), loggedUser.getSlug());
            user.setChatNotification(unSeenChatsCount);
        }
        return userList;
    }


    public Contact addNewContact(User loggedUser ,String contactSlug){
        Integer userFound = chatRepository.isUserExistsInChatList(loggedUser.getSlug(),contactSlug);
        if(userFound > 0) return null;
        /* check contact user exists or not */
        User contactUser = userRepository.findUserBySlug(contactSlug);
        if(contactUser == null) throw new MyException("Not a valid contact");
        Contact contacts = Contact.builder()
                .userId(loggedUser.getId())
                .contact(contactUser)
                .build();
        return contactRepository.save(contacts);
    }

}
