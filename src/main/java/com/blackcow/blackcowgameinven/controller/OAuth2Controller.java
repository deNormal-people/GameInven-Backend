package com.blackcow.blackcowgameinven.controller;

import com.blackcow.blackcowgameinven.dto.ApiResponse;
import com.blackcow.blackcowgameinven.dto.JwtToken;
import com.blackcow.blackcowgameinven.dto.LoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login/oauth2")
public class OAuth2Controller {

    @PostMapping("/google")
    public ResponseEntity<ApiResponse> oAuth2Login(@RequestBody LoginDTO loginDTO) throws Exception {

        //로그인 성공시
        ApiResponse<JwtToken> apiResponse = ApiResponse.<JwtToken>builder("로그인 성공")
                .data(JwtToken.builder()
                        .accessToken("")
                        .refreshToken("")
                        .build())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
