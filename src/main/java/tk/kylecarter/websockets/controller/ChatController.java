package tk.kylecarter.websockets.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import tk.kylecarter.websockets.model.ChatMessage;
import tk.kylecarter.websockets.model.OutputMessage;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    private SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messageTemplate) {
        this.messagingTemplate = messageTemplate;
    }

    @MessageMapping("chat")
    @SendTo("/topic/messages")
    public OutputMessage send(ChatMessage message) throws Exception {
        String time = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }

    @MessageMapping("whisper.{userToCall}")
    public void whisper(Principal principal, ChatMessage message, @DestinationVariable String userToCall) {
        String time = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        messagingTemplate.convertAndSendToUser(userToCall, "/topic/messages", new OutputMessage(principal.getName(), message.getText(), time));
    }
}
