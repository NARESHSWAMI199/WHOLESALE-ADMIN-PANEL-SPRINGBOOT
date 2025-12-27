package com.sales.wholesaler.controller;

import com.sales.dto.ContactDto;
import com.sales.entities.Contact;
import com.sales.entities.User;
import com.sales.global.ConstantResponseKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("contacts")
@RequiredArgsConstructor
public class ContactController extends WholesaleServiceContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @GetMapping("all")
    public ResponseEntity<List<User>> getAllContactsByUserId(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        safeLog.info(logger,"Fetching all contacts for logged user: {}", loggedUser.getId());
        List<User> allContactsByUserId = contactService.getAllContactsByUserId(loggedUser,request);
        return new ResponseEntity<>(allContactsByUserId, HttpStatus.valueOf(200));
    }

    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> addNewContactInContactList(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        safeLog.info(logger,"Adding new contact for logged user: {}", loggedUser.getId());
        Contact contact = contactService.addNewContact(loggedUser, contactDto.getContactSlug());
        if(contact != null){
            safeLog.info(logger,"Contact added successfully for user: {}", loggedUser.getId());
            result.put("contact",contact.getContactUser());
            result.put(ConstantResponseKeys.MESSAGE,"Your contact has been successfully added.");
            result.put(ConstantResponseKeys.STATUS, 200);
        }else {
            logger.error("Failed to add contact for user: {}", loggedUser.getId());
            result.put(ConstantResponseKeys.MESSAGE,"Something went wrong during insert your contact");
            result.put(ConstantResponseKeys.STATUS, 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }

    @PostMapping("remove")
    public ResponseEntity<Map<String,Object>> removeContactAndHisChat(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        safeLog.info(logger,"Removing new contact for logged user: {}", loggedUser.getId());
        int contact = contactService.removeContact(loggedUser, contactDto.getContactSlug(),contactDto.getDeleteChats());
        if(contact>0){
            safeLog.info(logger,"Contact removed successfully for user: {}", loggedUser.getId());
            result.put(ConstantResponseKeys.MESSAGE,"Your contact has been successfully removed.");
            result.put(ConstantResponseKeys.STATUS, 200);
        }else {
            logger.error("Failed to removed contact for user: {}", loggedUser.getId());
            result.put(ConstantResponseKeys.MESSAGE,"No contact found to delete.");
            result.put(ConstantResponseKeys.STATUS, 404);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get(ConstantResponseKeys.STATUS)));
    }

}
