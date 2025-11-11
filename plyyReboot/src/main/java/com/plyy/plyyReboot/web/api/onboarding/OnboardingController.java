package com.plyy.plyyReboot.web.api.onboarding;

import com.plyy.plyyReboot.config.security.jwt.JwtTokenProvider;
import com.plyy.plyyReboot.config.security.RedisService;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.web.api.onboarding.dto.OnboardingResponseDto;
import com.plyy.plyyReboot.web.api.onboarding.dto.OnboardingRequestDto;
import com.plyy.plyyReboot.web.api.onboarding.dto.NicknameCheckResponseDto;
import com.plyy.plyyReboot.web.api.dto.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    /**
     * 2.1 닉네임 중복 확인
     * (ROLE_NEW_USER, ROLE_USER 모두 사용 가능)
     */
    @GetMapping("/nickname/check")
    public ResponseEntity<NicknameCheckResponseDto> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isAvailable = onboardingService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(new NicknameCheckResponseDto(isAvailable));
    }

    /**
     * 2.2 온보딩 완료 (닉네임 + 선호 장르/무드 등록)
     * (ROLE_NEW_USER만 호출 가능)
     */
    @PostMapping("/onboarding/complete")
    public ResponseEntity<OnboardingResponseDto> completeOnboarding(
            @AuthenticationPrincipal Long userId, // JwtAuthenticationFilter가 넣어준 ID
            @Valid @RequestBody OnboardingRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response // 쿠키를 설정하기 위해 필요
    ) {

        // 1. 서비스 레이어를 호출해 닉네임, 장르/무드 저장 및
        //    DB의 role을 "ROLE_USER"로 업그레이드
        User updatedUser = onboardingService.completeOnboarding(userId, requestDto);

        // 2. 역할이 변경되었으므로, 새 토큰("ROLE_USER")을 즉시 발급
        TokenResponse newTokens = jwtTokenProvider.createTokens(
                updatedUser.getId(),
                updatedUser.getRole() // "ROLE_USER"가 담김
        );

        // 3. Redis의 Refresh Token도 새 것으로 덮어쓰기
        try {
            redisService.saveRefreshToken(updatedUser.getId(), newTokens.refreshToken());
            log.info("온보딩 완료: 새 Refresh Token 저장 성공 (UserID: {})", updatedUser.getId());
        } catch (Exception e) {
            log.error("온보딩 완료: 새 Refresh Token 저장 실패 (UserID: {})", updatedUser.getId(), e);
            // (에러가 나도 일단 진행하지만, 로깅은 필수)
        }

        // 4. 프론트엔드에 새 토큰을 쿠키로 전달
        addCookie(request, response, "refreshToken", newTokens.refreshToken(), 604800, true);
        addCookie(request, response, "accessToken", newTokens.accessToken(), 86400, false);

        // 5. 응답 본문에 완료 메시지와 새 토큰을 담아 반환
        OnboardingResponseDto responseBody = new OnboardingResponseDto(
                "Onboarding completed successfully",
                updatedUser,
                newTokens
        );

        return ResponseEntity.ok(responseBody);
    }

    // (OAuth2AuthenticationSuccessHandler에서 가져온 쿠키 헬퍼)
    private void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }
}