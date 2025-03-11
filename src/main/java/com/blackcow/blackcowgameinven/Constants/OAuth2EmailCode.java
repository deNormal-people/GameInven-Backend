package com.blackcow.blackcowgameinven.Constants;

public enum OAuth2EmailCode {
    GOOGLE("email"),
    KAKAO("kakao_account.email");

    private final String emailCode;

    OAuth2EmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public String getEmailCode() {
        return emailCode;
    }
}
