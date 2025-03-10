package com.blackcow.blackcowgameinven.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT관련 로직
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private SecretKey secretKey;

    //모든 DI가 완료된 후 실행
    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /***
     * Access Token 발행
     * @param authentication 사용자 계정
     * @return 신규 Access 토큰 값
     */
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, 1000 * 60 * 60);          // 1시간
    }

    /***
     * Refresh Token 발행
     * @param authentication 사용자 계정
     * @return 신규 Refresh 토큰 값
     */
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, 1000 * 60 * 60 * 24 * 7);      // 1주일
    }

    /**
     * 토큰 발행
     * @param authentication 인증정보
     * @param expiration 유효기간
     * @return 토큰값
     */
    public String generateToken(Authentication authentication, long expiration) {
        Object principal = authentication.getPrincipal();
        String email;
        List<String> roles;

        // OAuth2 로그인 사용자인 경우 (Google, Kakao 등)
        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email"); // Google OAuth에서는 email 사용
            roles = List.of("ROLE_USER"); // 기본적으로 OAuth 사용자는 "ROLE_USER" 부여
        }
        // 일반 로그인 사용자인 경우
        else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        } else {
            throw new IllegalArgumentException("지원되지 않는 인증 사용자 타입입니다.");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .claim("roles", roles) // ✅ 역할 정보 추가
                .compact();
    }


    /***
     * 토큰으로 부터 사용자 ID를 가져온다.
     * @param token
     * @return 정상 토큰일경우 사용자 ID를 리턴하고 아닐경우 "" 공백 문자열을 리턴한다.
     */
    public String getUsernameFromToken(String token) {

        Claims claims = getValidateToken(token);

        if(claims != null) {
            return claims.getSubject();
        }
        return "";
    }

    /**
     * 토큰으로 부터 계정권한을 가져온다.
     * @param token access/refresh
     * @return 정상값일 경우 대문자 문자열로된 권한값을 리턴하고 아닐경우 "" 공백 문자열을 리턴한다.
     */
    public String getRoleFromToken(String token) {
        Claims claims = getValidateToken(token);
        if(claims != null) {
            return claims.get("roles", String.class);
        }
        return "";
    }

    /***
     * 토큰을 검증후에 Claims 오브젝트로 돌려준다.
     * @param token access/refresh
     * @return  정상 토큰이 아닐 경우 null 을 리턴함
     */
    public Claims getValidateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException ex) {
            // 유효하지 않은 JWT 토큰
        } catch (ExpiredJwtException ex) {
            // 만료된 JWT 토큰
        } catch (UnsupportedJwtException ex) {
            // 지원되지 않는 JWT 토큰
        } catch (IllegalArgumentException ex) {
            // JWT 토큰 compact of handler are invalid
        }catch (Exception ex){
            // 그 밖의 오류
        }
        return null;
    }

}
