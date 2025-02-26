package com.blackcow.blackcowgameinven.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class OAuthUserDTO {

    String username;

    Map<String, Object> attributes;

}
