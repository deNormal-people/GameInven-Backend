package com.blackcow.blackcowgameinven.service;

import com.blackcow.blackcowgameinven.serviceinterface.OAuthUserInfo;

import java.util.Map;

public class OAuthGoogleUserInfo implements OAuthUserInfo {
    @Override
    public String getEmail(Map<String, Object> attributes) {
        return attributes.get("email").toString();
    }
}
