package com.sales.admin.controllers;


import com.sales.dto.ItemCommentsFilterDto;
import com.sales.entities.ItemComments;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/item/comments")
public class ItemCommentController extends ServiceContainer {

    @PostMapping("all")
    public ResponseEntity<List<ItemComments>> getAllUsers(@RequestBody ItemCommentsFilterDto itemCommentsFilterDto, HttpServletRequest httpServletRequest) {
        List<ItemComments> itemCommentsPage =  itemCommentService.getALlItemComment(itemCommentsFilterDto);
        return new ResponseEntity<>(itemCommentsPage, HttpStatus.OK);
    }


}
