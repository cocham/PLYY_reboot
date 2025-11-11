package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.config.security.jwt.JwtTokenProvider;
import com.plyy.plyyReboot.web.api.dto.TokenResponse;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.config.security.RedisService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    private static final String FRONTEND_CALLBACK_URL = "http://localhost:3000/auth/callback";
    private static final String FRONTEND_ONBOARDING_URL = "http://localhost:3000/onboarding";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 1. OAuth2User에서 attributes 맵
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. registrationId 가져오기
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // 3. UserService가 Principal의 "name"으로 이메일을 넣어줌
        String email = authentication.getName();

        // 4. 이메일로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("OAuth2 인증 성공 후 유저를 찾을 수 없습니다."));

        boolean isNewUser = user.getRole().equals("ROLE_NEW_USER");

        // 5. JWT 토큰 생성
        TokenResponse tokens = jwtTokenProvider.createTokens(user.getId(), user.getRole());

        // 6. Redis에 Refresh Token 저장 (동기)
        try {
            // (RedisConfig에서 설정한 Serializer를 사용하는 동기 메서드 호출)
            redisService.saveRefreshToken(user.getId(), tokens.refreshToken());
            log.info("Refresh Token 저장 성공 (UserID: {})", user.getId());

        } catch (Exception e) {
            // (만약 RedisConfig 설정이 잘못되었거나 연결 실패 시, 여기에 ERROR가 찍힘)
            log.error("Refresh Token 저장 실패 (UserID: {})", user.getId(), e);
        }

        addCookie(request, response, "refreshToken", tokens.refreshToken(), 604800, true);
        addCookie(request, response, "accessToken", tokens.accessToken(), 86400, false);
        String targetUrl;
        if (isNewUser) {
            targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_ONBOARDING_URL)
                    .queryParam("isNewUser", true)
                    .build().toUriString();
        } else {
            targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_CALLBACK_URL)
                    .build().toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 모든 경로에서 접근 가능
        cookie.setMaxAge(maxAge); // 초 단위로 설정
        cookie.setHttpOnly(httpOnly); // JS 접근 차단 (refresh token에 필수)
        cookie.setSecure(request.isSecure()); // HTTPS(운영)에서만 Secure 플래그 설정, 로컬(http)에서도 테스트 가능하게 변경
        response.addCookie(cookie);
    }
}