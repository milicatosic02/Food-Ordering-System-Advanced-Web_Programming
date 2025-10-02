package com.example.demo.controller;

import com.example.demo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class MessageController {

    private SimpMessagingTemplate simpMessagingTemplate;
    private SimpUserRegistry simpUserRegistry;

    @Autowired
    public MessageController(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageMapping("/send-message")
    @SendTo("/topic/order-status")
    public Message send(@Payload Message message, StompHeaderAccessor stompHeaderAccessor) throws Exception{
        //message.setFrom(stompHeaderAccessor.getUser().getName());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        System.out.println("[" + time + "] Message sent.");

        return message;
    }

}
