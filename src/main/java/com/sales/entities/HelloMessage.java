package com.sales.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class HelloMessage {

    private String message;
    private String sender;
    private String type;
}
