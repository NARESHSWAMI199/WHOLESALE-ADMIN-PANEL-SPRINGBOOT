package com.sales.wholesaler.controller;

import com.sales.dto.ContactDto;
import com.sales.entities.Contact;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
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
public class ContactController extends WholesaleServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @GetMapping("all")
    public ResponseEntity<List<User>> getAllContactsByUserId(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        logger.info("Fetching all contacts for logged user: {}", loggedUser.getId());
        List<User> allContactsByUserId = contactService.getAllContactsByUserId(loggedUser,request);
        return new ResponseEntity<>(allContactsByUserId, HttpStatus.valueOf(200));
    }

    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> addNewContactInContactList(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        logger.info("Adding new contact for logged user: {}", loggedUser.getId());
        Contact contact = contactService.addNewContact(loggedUser, contactDto.getContactSlug());
        if(contact != null){
            logger.info("Contact added successfully for user: {}", loggedUser.getId());
            result.put("message","Your contact has been successfully inserted");
            result.put("status", 200);
        }else {
            logger.error("Failed to add contact for user: {}", loggedUser.getId());
            result.put("message","Something went wrong during insert your contact");
            result.put("status", 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}
