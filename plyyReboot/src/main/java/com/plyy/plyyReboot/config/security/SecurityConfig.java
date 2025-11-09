package com.plyy.plyyReboot.config.security;

import com.plyy.plyyReboot.client.oauth.CustomOAuth2UserService;
import com.plyy.plyyReboot.client.oauth.OAuth2AuthenticationSuccessHandler;
import com.plyy.plyyReboot.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // [수정 1] JwtAuthenticationFilter 주입 (주석 해제)
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // (1) 세션 STATELESS, CSRF/FormLogin/HttpBasic 비활성화
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // (2) API 엔드포인트별 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // (permitAll() 경로들을 명확하게 그룹화)
                .requestMatchers(
                        "/",
                        "/static/**",
                        "/index.html",
                        "/login/oauth2/code/**",    // OAuth 리다이렉트
                        "/oauth2/authorization/**", // OAuth 로그인 시도
                        "/auth/reissue",            // 토큰 재발급
                        "/auth/logout",             // 로그아웃
                        "/docs/**",                 // API 문서
                        "/actuator/**"              // 모니터링
                ).permitAll()
                .requestMatchers("/api/v1/curator/**").hasRole("CURATOR")
                .requestMatchers("/api/v1/**").hasAnyRole("USER", "CURATOR")
                .anyRequest().authenticated()
        );

        // (3) OAuth2 로그인 설정
        http.oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                // [수정 2] .http.addFilterBefore(...)는 여기에 있으면 안 됩니다.
        );

        // (4) [수정 3] JwtAuthenticationFilter를 올바른 위치(UsernamePasswordAuthenticationFilter 앞)에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // (CORS 설정 Bean)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}