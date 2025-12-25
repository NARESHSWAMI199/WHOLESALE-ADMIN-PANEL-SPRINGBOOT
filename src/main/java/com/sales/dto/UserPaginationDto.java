package com.sales.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserPaginationDto {
    Integer userId;
    Integer paginationId;
    Integer rowsNumber;
}
