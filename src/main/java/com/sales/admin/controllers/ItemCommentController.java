package com.sales.admin.controllers;


import com.sales.admin.repositories.ItemCommentRepository;
import com.sales.admin.services.RepoContainer;
import com.sales.dto.ItemCommentsFilterDto;
import com.sales.dto.UserSearchFilters;
import com.sales.entities.ItemComments;
import com.sales.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("admin/item/comments")
public class ItemCommentController extends ServiceContainer {

    @PostMapping("all")
    public ResponseEntity<Page<ItemComments>> getAllUsers(@RequestBody ItemCommentsFilterDto itemCommentsFilterDto,  HttpServletRequest httpServletRequest) {
        Page<ItemComments> itemCommentsPage =  itemCommentService.getALlItemComment(itemCommentsFilterDto);
        return new ResponseEntity<>(itemCommentsPage, HttpStatus.OK);
    }


}
