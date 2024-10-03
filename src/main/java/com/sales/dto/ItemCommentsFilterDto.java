package com.sales.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCommentsFilterDto extends SearchFilters{
    String message;
    Integer userId;
    String userSlug;
    String itemSlug;
    int itemId;
    int parentId = 0;
}
