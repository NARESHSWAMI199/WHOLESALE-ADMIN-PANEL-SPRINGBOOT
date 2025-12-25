package com.sales.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
public class ChatRoomDto {
    Long id;
    String name;
    String description;
    String slug;
    List<String> users;
}
