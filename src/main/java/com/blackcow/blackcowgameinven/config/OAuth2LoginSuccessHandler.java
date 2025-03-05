package com.blackcow.blackcowgameinven.config;

import com.blackcow.blackcowgameinven.Constants.OAuth2EmailCode;
import com.blackcow.blackcowgameinven.dto.JwtToken;
import com.blackcow.blackcowgameinven.dto.UserDTO;
import com.blackcow.blackcowgameinven.service.JwtService;
import com.blackcow.blackcowgameinven.service.UserServcie;
import com.blackcow.blackcowgameinven.util.JsonUtil;
import com.blackcow.blackcowgameinven.util.RandomPasswordGenerator;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserServcie userServcie;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtService jwtService, UserServcie userServcie) {
        this.jwtService = jwtService;
        this.userServcie = userServcie;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String email =
                switch (registrationId){
                    case "google" -> oAuth2User.getAttribute(OAuth2EmailCode.GOOGLE.getEmailCode());
                    case "kakao" -> {
                        Map<String, Object> kakaoAccount = oAuth2User.getAttribute(OAuth2EmailCode.KAKAO.getEmailCode());
                        yield kakaoAccount != null? (String)kakaoAccount.get("email"):null;
                    }
                    default -> null;
                };
        oAuth2User.getAttribute("email");

        //기존 사용자 인지 조회
        try {
            UserDetails userDetails = userServcie.loadUserByUsername(email);
        }catch (UsernameNotFoundException unfe){
            try {
                if(email != null) {
                    userServcie.createuser(new UserDTO(email, RandomPasswordGenerator.generatePassword(), email, ""));
                }else{
                    throw new UsernameNotFoundException("OAuth에서 E-mail 정보를 가져오지못함.");
                }
            }catch (SQLException se){
                logger.error("OAuth2 연동 회원가입 실패 : " + email);
                logger.error(se.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "회원가입 실패");
                return;
            } catch (UsernameNotFoundException nfe) {
                logger.error(nfe.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, nfe.getMessage());
                return;
            }
        }
        String refreshToken = jwtService.generateRefreshToken(authentication);
        String accessToken = jwtService.generateAccessToken(authentication);

        JwtToken tonkens = JwtToken.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();

        //JSON 형태로 변환
        String tokenJson = JsonUtil.toJson(tonkens);
        String encodedTokenJson = URLEncoder.encode(tokenJson, StandardCharsets.UTF_8);

        //프런트 리다이렉션 URL
        response.sendRedirect("http://localhost:3000?token=" + encodedTokenJson);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
