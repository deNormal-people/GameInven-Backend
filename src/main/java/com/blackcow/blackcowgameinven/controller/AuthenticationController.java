package com.blackcow.blackcowgameinven.controller;

import com.blackcow.blackcowgameinven.dto.ApiResponse;
import com.blackcow.blackcowgameinven.dto.JwtToken;
import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.service.JwtService;
import com.blackcow.blackcowgameinven.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDTO loginDTO){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));

            return ResponseEntity.ok(
                    JwtToken.builder()
                            .accessToken(jwtService.generateAccessToken(authentication))
                            .refreshToken(jwtService.generateRefreshToken(authentication))
                            .build()
            );
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder("인증오류").build());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Validated @RequestBody JwtToken jwtToken) {

        String refreshToken = jwtToken.getRefreshToken();

        if (jwtService.getValidateToken(refreshToken) != null) {
            String id = jwtService.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(id);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String newAccessToken = jwtService.generateAccessToken(authentication);

            return ResponseEntity.ok(ApiResponse.builder("Refresh Token")
                    .data(new JwtToken(newAccessToken, refreshToken))
                    .build());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder("만료된 토큰입니다.").build());
    }

    @PostMapping(value = "/dupl", produces = "application/json")
    public ResponseEntity<?> dupleCheck(@RequestBody UserDTO userDTO) {
        if(userService.duplicationCheck(userDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder("중복된 계정").build());
        }
        return ResponseEntity.ok(ApiResponse.builder("사용가능한 게정").build());
    }

    @PostMapping(value = "signup", produces = "application/json")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO){
        try{
            userService.createuser(userDTO);
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
