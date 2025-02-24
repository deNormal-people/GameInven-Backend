package com.blackcow.blackcowgameinven.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private final long accessTokenExpiration = 1000 * 60 * 60;              //1시간
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 7;    //1주일

    /***
     * Access Token 발행
     * @param authentication
     * @return
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration); // 1시간

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    /***
     * Refresh Token 발행
     * @param authentication
     * @return
     */
    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration); // 1주일

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    /***
     * 토큰에서 사용자 ID 추출
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /***
     * 토큰 검증
     * @param token access/refresh
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            // 유효하지 않은 JWT 토큰
        } catch (ExpiredJwtException ex) {
            // 만료된 JWT 토큰
        } catch (UnsupportedJwtException ex) {
            // 지원되지 않는 JWT 토큰
        } catch (IllegalArgumentException ex) {
            // JWT 토큰 compact of handler are invalid
        }
        return false;
    }

}
