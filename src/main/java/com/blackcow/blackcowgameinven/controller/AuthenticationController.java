package com.blackcow.blackcowgameinven.controller;

import com.blackcow.blackcowgameinven.dto.ApiResponse;
import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.service.JwtService;
import com.blackcow.blackcowgameinven.service.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            return authorizationService.generateTokenResponse(authentication);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder("인증 오류").build());
        }
    }




    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // 쿠키에 refresh token이 없으면 401 반환
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder("Refresh token이 없습니다.").build());
        }

        // refresh token 검증
        if (!jwtService.isValidToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.builder("만료된 토큰입니다.").build());
        }

        // refresh token에서 사용자 정보 추출
        String id = jwtService.getUsernameFromToken(refreshToken);
        UserDetails userDetails = authorizationService.loadUserByUsername(id);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 새로운 access token 발급
        String newAccessToken = jwtService.generateAccessToken(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)  // access token을 헤더에 포함
                .body(ApiResponse.builder("Access Token 재발급 완료").build());
    }


    @PostMapping(value = "/check-duplicate", produces = "application/json")
    public ResponseEntity<ApiResponse<?>> dupleCheck(@RequestBody UserDTO userDTO) {
        if(authorizationService.duplicationCheck(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder("중복된 계정").build());
        }
        return ResponseEntity.ok(ApiResponse.builder("사용가능한 게정").build());
    }

    @PostMapping(value = "signup", produces = "application/json")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO){
        try{
            authorizationService.createuser(userDTO);
        }catch (SQLException ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder(ex.getMessage()).build());
        }catch (IllegalArgumentException iex){
            log.error(iex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder("잘못된 비밀번호 입니다.").build());
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder(ex.getMessage()).build());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder("회원가입 성공")
                .data(userDTO.getUsername())
                .build());
    }
    
}
