package com.sales.entities;


import lombok.Data;

//@Getter
//@Setter
//@AllArgsConstructor
//@Builder
@Data
public class Message {

    private String message;
    private String senderKey;
    private String receiverKey;
    private String type;
    private String sessionId;
    private Long time;
    private Long lastSeen;
}
