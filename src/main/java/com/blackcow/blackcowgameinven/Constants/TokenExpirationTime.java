package com.blackcow.blackcowgameinven.Constants;

public enum TokenExpirationTime {
    ACCESS_TOKEN(5 * 60 * 1000),                    //5분
    REFRESH_TOKEN(7 * 24 * 60 * 60 * 1000);         //7일

    long expirationTime;

    TokenExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
