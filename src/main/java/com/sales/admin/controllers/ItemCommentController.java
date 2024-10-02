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
