package com.blackcow.blackcowgameinven.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;
    private String roomName;

    public static ChatRoomDTO Create(String name) {
        ChatRoomDTO room = new ChatRoomDTO();
        room.roomId = UUID.randomUUID().toString(); // 고유 ID 생성
        room.roomName = name;
        return room;
    }
}
