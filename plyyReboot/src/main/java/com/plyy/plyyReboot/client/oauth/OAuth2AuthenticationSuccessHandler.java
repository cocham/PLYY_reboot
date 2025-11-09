package com.plyy.plyyReboot.client.oauth;

import com.plyy.plyyReboot.config.security.jwt.JwtTokenProvider;
import com.plyy.plyyReboot.web.api.dto.TokenResponse;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.config.security.RedisService;
import com.plyy.plyyReboot.client.oauth.dto.GoogleOAuth2UserInfo;
import com.plyy.plyyReboot.client.oauth.dto.KakaoOAuth2UserInfo;
import com.plyy.plyyReboot.client.oauth.dto.NaverOAuth2UserInfo;
import com.plyy.plyyReboot.client.oauth.dto.OAuth2UserInfo;
import jakarta.servlet.ServletException;
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
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    // (프론트엔드가 토큰을 받을 콜백 URL)
    private static final String FRONTEND_CALLBACK_URL = "http://localhost:3000/auth/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 1. registrationId와 attributes로 유저 정보 파싱
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        OAuth2UserInfo userInfo = createUserInfo(registrationId, attributes);

        // 2. 이메일로 DB에서 유저 조회 (CustomOAuth2UserService에서 이미 저장/수정됨)
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("OAuth2 인증 성공 후 유저를 찾을 수 없습니다."));

        // 3. JWT 토큰 생성
        TokenResponse tokens = jwtTokenProvider.createTokens(user.getId(), user.getRole());

        // 4. Redis에 Refresh Token 저장 (동기)
        try {
            // (RedisConfig에서 설정한 Serializer를 사용하는 동기 메서드 호출)
            redisService.saveRefreshToken(user.getId(), tokens.refreshToken());

            log.info("Refresh Token 저장 성공 (UserID: {})", user.getId());

        } catch (Exception e) {
            // (만약 RedisConfig 설정이 잘못되었거나 연결 실패 시, 여기에 ERROR가 찍힘)
            log.error("Refresh Token 저장 실패 (UserID: {})", user.getId(), e);
        }

        // 5. 프론트엔드로 Access/Refresh 토큰을 담아 리다이렉트
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_CALLBACK_URL)
                .queryParam("accessToken", tokens.accessToken())
                .queryParam("refreshToken", tokens.refreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // (Helper) CustomOAuth2UserService와 동일한 파싱 로직
    private OAuth2UserInfo createUserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("kakao")) {
            return new KakaoOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("naver")) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }
}