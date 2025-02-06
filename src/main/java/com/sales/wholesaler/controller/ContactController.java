package com.sales.wholesaler.controller;


import com.sales.dto.ContactDto;
import com.sales.entities.Contact;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("contacts")
public class ContactController extends WholesaleServiceContainer {


    @GetMapping("all")
    public ResponseEntity<List<User>> getAllContactsByUserId(HttpServletRequest request){
        User loggedUser = (User) request.getAttribute("user");
        List<User> allContactsByUserId = contactService.getAllContactsByUserId(loggedUser,request);
        return new ResponseEntity<>(allContactsByUserId, HttpStatus.valueOf(200));
    }


    @PostMapping("add")
    public ResponseEntity<Map<String,Object>> addNewContactInContactList(@RequestBody ContactDto contactDto, HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        User loggedUser = (User) request.getAttribute("user");
        Contact contact = contactService.addNewContact(loggedUser, contactDto.getContactSlug());
        if(contact != null){
            result.put("message","Your contact has been successfully inserted");
            result.put("status", 200);
        }else {
            result.put("message","Something went wrong during insert your contact");
            result.put("status", 400);
        }
        return new ResponseEntity<>(result,HttpStatus.valueOf((Integer) result.get("status")));
    }

}
