package com.blackcow.blackcowgameinven.controller;

import com.blackcow.blackcowgameinven.dto.JwtToken;
import com.blackcow.blackcowgameinven.dto.LoginDTO;
import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.service.JwtService;
import com.blackcow.blackcowgameinven.service.UserServcie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserServcie userServcie;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDTO){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getId(), loginDTO.getPassword()));

            return ResponseEntity.ok(
                    JwtToken.builder()
                            .accessToken(jwtService.generateAccessToken(authentication))
                            .refreshToken(jwtService.generateRefreshToken(authentication))
                            .build()
            );
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ID 혹은 PW 검증 불가");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Validated @RequestBody JwtToken jwtToken) {

        String refreshToken = jwtToken.getRefreshToken();

        if (jwtService.validateToken(refreshToken)) {
            String id = jwtService.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userServcie.loadUserByUsername(id);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String newAccessToken = jwtService.generateAccessToken(authentication);

            return ResponseEntity.ok(new JwtToken(newAccessToken, refreshToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("만료된 토큰입니다.");
    }

    @PostMapping("/dupl")
    public ResponseEntity<?> dupleCheck(@RequestBody String username){
        if(!userServcie.duplicationCheck(username)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 계정");
        }
        return ResponseEntity.ok("사용가능한 게정");
    }

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@RequestBody UserDTO userDTO){
        try{
            userServcie.createuser(userDTO);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복계정 오류");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO.getUsername());
    }
    
}
