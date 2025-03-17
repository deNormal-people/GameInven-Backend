package com.blackcow.blackcowgameinven.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ChatRoom {
    @Id
    private Long seq;

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Long getSeq() {
        return seq;
    }
}
