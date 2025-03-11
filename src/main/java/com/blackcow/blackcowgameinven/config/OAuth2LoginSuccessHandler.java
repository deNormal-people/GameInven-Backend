package com.blackcow.blackcowgameinven.config;

import com.blackcow.blackcowgameinven.Constants.OAuth2EmailCode;
import com.blackcow.blackcowgameinven.dto.JwtToken;
import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.service.JwtService;
import com.blackcow.blackcowgameinven.service.AuthorizationService;
import com.blackcow.blackcowgameinven.util.JsonUtil;
import com.blackcow.blackcowgameinven.util.RandomPasswordGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtService jwtService, AuthorizationService authorizationService) {
        this.jwtService = jwtService;
        this.authorizationService = authorizationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String email =
                switch (registrationId) {
                    case "google" -> oAuth2User.getAttribute(OAuth2EmailCode.GOOGLE.getEmailCode());
                    case "kakao" -> {
                        Map<String, Object> kakaoAccount = oAuth2User.getAttribute(OAuth2EmailCode.KAKAO.getEmailCode());
                        yield kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
                    }
                    default -> null;
                };

        if (email == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth에서 E-mail 정보를 가져오지 못함.");
            return;
        }

        // 기존 사용자 조회 또는 자동 회원가입
        UserDTO oAuthUser;
        try {
            UserDetails userDetails = authorizationService.loadUserByUsername(email);
            oAuthUser = new UserDTO(userDetails.getUsername(), userDetails.getPassword(), userDetails.getUsername(), "");
        } catch (UsernameNotFoundException unfe) {
            oAuthUser = new UserDTO(email, RandomPasswordGenerator.generatePassword(), email, "");
            try {
                authorizationService.createuser(oAuthUser);
            }catch (Exception e){
                throw new ServletException(e);
            }
        }

        // OAuth2 사용자를 Authentication 객체로 변환
        Authentication oauthAuthentication =
                new UsernamePasswordAuthenticationToken(oAuthUser.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // 기존 `/login` 로직과 동일하게 처리
        ResponseEntity<?> tokenResponse = authorizationService.generateTokenResponse(oauthAuthentication);

        // OAuth2는 리다이렉트해야 하므로 응답 헤더에서 토큰을 추출
        String accessToken = tokenResponse.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 프론트엔드로 리다이렉트
        response.sendRedirect("http://localhost:3000?token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8));

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
