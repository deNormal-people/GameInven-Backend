package com.blackcow.blackcowgameinven.controller;

import com.blackcow.blackcowgameinven.dto.chat.ChatMessageDTO;
import com.blackcow.blackcowgameinven.model.ChatRoom;
import com.blackcow.blackcowgameinven.repository.ChatRepository;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatRepository chatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
    }

    @MessageMapping("/send/{roomcode}")
    public void sendMessage(@DestinationVariable String roomcode, @Payload ChatMessageDTO message) {
        if (message == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("메시지는 비어 있을 수 없습니다.");
        }

        String destination = String.format("/topic/%s", roomcode);
        messagingTemplate.convertAndSend(destination, message);
    }

    @MessageExceptionHandler
    public void handleException(Throwable exception, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", exception.getMessage());
    }



    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoom> getAllRooms() {
        return chatRepository.findAllRooms();
    }

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRepository.createRoom(name);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ChatRoom getRoom(@PathVariable String roomId) {
        return chatRepository.findRoomById(roomId);
    }

}
