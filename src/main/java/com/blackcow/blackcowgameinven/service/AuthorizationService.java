package com.blackcow.blackcowgameinven.service;

import com.blackcow.blackcowgameinven.Constants.TokenExpirationTime;
import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.model.User;
import com.blackcow.blackcowgameinven.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
        }
        throw new UsernameNotFoundException(username);
    }

    /***
     * 아이디 중복체크
     */
    public boolean duplicationCheck(String username){
        return userRepository.existsUserByUsername(username);
    }

    public void createuser(UserDTO userDTO) throws RuntimeException, SQLException, IllegalArgumentException{
        //2차 중복검증
        if(duplicationCheck(userDTO.getUsername())){
            throw new RuntimeException("중복된 계정입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User newUser = User.builder()
                            .username(userDTO.getUsername())
                            .password(encodedPassword)
                            .email(userDTO.getEmail())
                            .phone(userDTO.getPhone())
                            .build();


        userRepository.save(newUser);
    }

    public ResponseEntity<?> generateTokenResponse(Authentication authentication) {

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        // ✅ refresh token을 HttpOnly Secure Cookie에 저장
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)   // JavaScript 접근 차단 (XSS 방어)
                .secure(true)     // HTTPS에서만 전송
                .path("/")        // 모든 경로에서 접근 가능
                .maxAge(TokenExpirationTime.REFRESH_TOKEN.getExpirationTime()) // 7일간 유지
                .sameSite("Strict") // CSRF 공격 방어
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)  // ✅ Access Token을 응답 헤더에 포함
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()) // ✅ Refresh Token을 HttpOnly Secure 쿠키로 설정
                .build();
    }


}
