package com.sales.admin.controllers;

import com.sales.dto.MessageDto;
import com.sales.entities.Greeting1;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting1 greeting(MessageDto message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting1("Hello, " + HtmlUtils.htmlEscape(message.getMessage()) + "!");
    }

    @GetMapping("/index")
    public String indexPage (){
        return "index";
    }

    @GetMapping("/chat2")
    public String chat2Page (){
        return "chat2";
    }

}