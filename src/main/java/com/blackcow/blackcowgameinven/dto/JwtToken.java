package com.blackcow.blackcowgameinven.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtToken {

    private String accessToken;
    private String refreshToken;

}
