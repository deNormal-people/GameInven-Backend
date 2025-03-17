package com.blackcow.blackcowgameinven.repository;

import com.blackcow.blackcowgameinven.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ChatRepository extends JpaRepository<ChatRoom, Object> {
}
