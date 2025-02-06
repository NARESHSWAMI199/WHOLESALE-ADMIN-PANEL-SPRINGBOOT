package com.sales.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatUserDto {
    
    String receiverSlug;
    String status;

}
