package com.blackcow.blackcowgameinven.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    JwtAuthenticationFilter jwtAuthenticationFilter;
    OAuth2LoginSuccessHandler loginSuccessHandler;

    /*
    ┌─────┐
    |  OAuth2LoginSuccessHandler  <-- (필드 주입) UserService
    ↑     ↓
    |  userService (필드 주입) PasswordEncoder
    ↑     ↓
    |  SecurityConfig  <-- (필드 주입) OAuth2LoginSuccessHandler
    └─────┘
     */
    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, /*빈 순환참조 방지*/@Lazy OAuth2LoginSuccessHandler loginSuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)                  //csrf 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //세션 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //request 검증규칙
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests
                            .requestMatchers("api/auth/**", "/login/oauth2/code/*", "/").permitAll()          //토큰발행전 인증관련 모든부분은 인증이 필요없음
                            .anyRequest().authenticated();
                })
                //OAuth 로그인
                .oauth2Login(oauth ->
                        oauth
                                //로그인 성공시 회원가입/JWT 토큰 발급(로그인) 및 리다이렉션
                                .successHandler(loginSuccessHandler)
                )
                .oauth2Client(Customizer.withDefaults())            //Google OAuth2
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Next.js 프론트엔드 도메인
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
