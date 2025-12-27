package com.sales.wholesaler.controller;


import com.sales.dto.ItemReviewsFilterDto;
import com.sales.entities.ItemReviews;
import com.sales.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wholesale/item/review")
@RequiredArgsConstructor
public class WholesaleItemReviewController extends WholesaleServiceContainer {

    private final com.sales.helpers.Logger safeLog;
    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemReviewController.class);

    @PostMapping("all")
    public ResponseEntity<Page<ItemReviews>> getAllReviews(@RequestBody ItemReviewsFilterDto ItemReviewsFilterDto, HttpServletRequest request) {
        safeLog.info(logger,"Starting getAllUsers method");
        User loggedUser = (User) request.getAttribute("user");
        Page<ItemReviews> ItemReviewsPage = wholesaleItemReviewService.getAllItemReview(ItemReviewsFilterDto, loggedUser);
        safeLog.info(logger,"Completed getAllUsers method");
        return new ResponseEntity<>(ItemReviewsPage, HttpStatus.OK);
    }

}
