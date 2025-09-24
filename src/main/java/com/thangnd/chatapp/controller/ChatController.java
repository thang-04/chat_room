package com.thangnd.chatapp.controller;

import com.thangnd.chatapp.entity.ChatMessage;
import com.thangnd.chatapp.entity.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // Handle messages sent to /app/chat.sendMessage
    // and broadcast them to /topic/public
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage
    ) {
        return chatMessage;
    }

    // Handle messages sent to /app/chat.addUser
    // and broadcast them to /topic/public
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    // send a notification to all subscribers of /topic/notifications
    @PostMapping("/api/notify")
    @ResponseBody
    public String notifyUser(@RequestBody NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications", "hello message");
        return "Broadcast message sent to all subscribers";
    }

    // send a notification to a specific user subscribed to /user/{username}/queue/notifications
    @PostMapping("/api/notify/user/{username}")
    @ResponseBody
    public String notifyUser(@PathVariable String username,
                             @RequestBody NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
        return "Notify sent to username " + username;
    }

}
