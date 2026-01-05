package com.sales.wholesaler.controller;


import com.sales.claims.AuthUser;
import com.sales.claims.SalesUser;
import com.sales.dto.ItemReviewsFilterDto;
import com.sales.entities.ItemReviews;
import com.sales.wholesaler.services.WholesaleItemReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wholesale/item/review")
@RequiredArgsConstructor
public class WholesaleItemReviewController  {

    private final WholesaleItemReviewService wholesaleItemReviewService;
    private static final Logger logger = LoggerFactory.getLogger(WholesaleItemReviewController.class);


    @PostMapping("all")
    public ResponseEntity<Page<ItemReviews>> getAllReviews(Authentication authentication, @RequestBody ItemReviewsFilterDto ItemReviewsFilterDto, HttpServletRequest request) {
        logger.debug("Starting getAllUsers method");
        AuthUser loggedUser = (SalesUser) authentication.getPrincipal();
        Page<ItemReviews> ItemReviewsPage = wholesaleItemReviewService.getAllItemReview(ItemReviewsFilterDto, loggedUser);
        logger.debug("Completed getAllUsers method");
        return new ResponseEntity<>(ItemReviewsPage, HttpStatus.OK);
    }

}
