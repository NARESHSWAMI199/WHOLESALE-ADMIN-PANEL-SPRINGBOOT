package com.sales.entities;


import lombok.*;

//@Getter
//@Setter
//@AllArgsConstructor
//@Builder
@Data
public class Message {

    private String message;
    private String sender;
    private String type;
    private String sessionId;
    private Long time;
}
