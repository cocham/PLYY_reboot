package com.plyy.plyyReboot.web.api.onboarding;

import com.plyy.plyyReboot.client.oauth.util.CookieUtil;
import com.plyy.plyyReboot.config.security.jwt.JwtTokenGenerator;
import com.plyy.plyyReboot.config.security.jwt.JwtProperties;
import com.plyy.plyyReboot.config.security.redis.RedisService;
import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.web.api.onboarding.dto.OnboardingResponseDto;
import com.plyy.plyyReboot.web.api.onboarding.dto.OnboardingRequestDto;
import com.plyy.plyyReboot.web.api.onboarding.dto.NicknameCheckResponseDto;
import com.plyy.plyyReboot.web.api.dto.TokenResponse;
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
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtProperties jwtProperties;
    private final RedisService redisService;

    // (2.1 닉네임 중복 확인 - 변경 없음)
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
        User updatedUser = onboardingService.completeOnboarding(userId, requestDto);

        var newTokens = jwtTokenGenerator.generateTokenPair(
                updatedUser.getId(),
                updatedUser.getRole() // "ROLE_USER"가 담김
        );

        try {
            redisService.saveRefreshToken(updatedUser.getId(), newTokens.refreshToken());
            log.info("온보딩 완료: 새 Refresh Token 저장 성공 (UserID: {})", updatedUser.getId());
        } catch (Exception e) {
            log.error("온보딩 완료: 새 Refresh Token 저장 실패 (UserID: {})", updatedUser.getId(), e);
        }

        CookieUtil.addCookie(
                request,
                response,
                jwtProperties.getCookieName(),
                newTokens.accessToken(),
                (int) (jwtProperties.getAccessTokenExpirationMs() / 1000),
                false
        );

        TokenResponse tokenDto = new TokenResponse(
                newTokens.accessToken(),
                newTokens.refreshToken()
        );

        OnboardingResponseDto responseBody = new OnboardingResponseDto(
                "Onboarding completed successfully",
                updatedUser,
                tokenDto
        );

        return ResponseEntity.ok(responseBody);
    }
}