package com.blackcow.blackcowgameinven.service;

import com.blackcow.blackcowgameinven.dto.OAuthUserDTO;
import com.blackcow.blackcowgameinven.serviceinterface.OAuthUserInfo;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.Map;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    public CustomOAuth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService) {

    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //OAuth 유저정보
        Map<String, Object> oAuth2UserAttribute = super.loadUser(userRequest).getAttributes();

        //third-party id
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        //user name
        String username = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthUserDTO oAuthUserDTO = OAuthUserDTO.builder()
                .username(username)
                .attributes(oAuth2UserAttribute)
                .build();



        return null;
    }
}
