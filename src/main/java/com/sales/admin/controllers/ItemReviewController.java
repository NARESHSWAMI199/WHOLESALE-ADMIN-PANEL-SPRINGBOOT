package com.sales.admin.controllers;

import com.sales.dto.ItemReviewsFilterDto;
import com.sales.entities.ItemReviews;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("admin/item/review")
public class ItemReviewController extends ServiceContainer {

    private static final Logger logger = LoggerFactory.getLogger(ItemReviewController.class);

    @PostMapping("all")
    public ResponseEntity<Page<ItemReviews>> getAllReviews(@RequestBody ItemReviewsFilterDto ItemReviewsFilterDto, HttpServletRequest httpServletRequest) {
        logger.info("Fetching all item comments with filters: {}", ItemReviewsFilterDto);
        Page<ItemReviews > itemReviewsPage = itemReviewService.getALlItemReview(ItemReviewsFilterDto);
        return new ResponseEntity<>(itemReviewsPage, HttpStatus.OK);
    }

}
