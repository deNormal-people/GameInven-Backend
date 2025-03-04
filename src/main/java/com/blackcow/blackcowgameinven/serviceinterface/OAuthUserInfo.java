package com.blackcow.blackcowgameinven.serviceinterface;

import java.util.Map;

public interface OAuthUserInfo {
    /**
     * OAuth에서 가져온 속성중 E-mail정보를 가져온다
     * @param attributes OAuth에서 가져온 사용자 속성정보
     * @return 이메일 문자열
     */
    public abstract String getEmail(Map<String, Object> attributes);
}
