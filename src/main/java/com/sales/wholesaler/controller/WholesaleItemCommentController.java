package com.sales.wholesaler.controller;


import com.sales.dto.ItemCommentsFilterDto;
import com.sales.entities.ItemComments;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("wholesale/item/comments")
public class WholesaleItemCommentController extends WholesaleServiceContainer {

    @PostMapping("all")
    public ResponseEntity<List<ItemComments>> getAllUsers(@RequestBody ItemCommentsFilterDto itemCommentsFilterDto, HttpServletRequest request) {
        User loggedUser = (User) request.getAttribute("user");
        List<ItemComments> itemCommentsPage =  wholesaleItemCommentService.getALlItemComment(itemCommentsFilterDto,loggedUser);
        return new ResponseEntity<>(itemCommentsPage, HttpStatus.OK);
    }


}
