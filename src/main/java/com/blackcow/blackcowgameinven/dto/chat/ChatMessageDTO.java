package com.blackcow.blackcowgameinven.dto.chat;

import com.blackcow.blackcowgameinven.Constants.MessageType;
import lombok.Getter;

@Getter
public class ChatMessageDTO {
    private String sender;
    private String content;
    private MessageType type;
    // Getters and Setters
}
