package com.example.demo.listeners;
import com.example.demo.model.Message;
import com.example.demo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {

    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Recieved a new web socket connection. Session: " + headerAccessor.getSessionId());

      //  this.messagingTemplate.convertAndSend("/topic/messages", message);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectedEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Recieved a new web socket connection. Session: " + headerAccessor.getSessionId());

       // this.messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
