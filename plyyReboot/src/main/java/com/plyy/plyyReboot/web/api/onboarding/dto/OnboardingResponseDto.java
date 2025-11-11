package com.plyy.plyyReboot.web.api.onboarding.dto;

import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.web.api.dto.TokenResponse;
import lombok.Getter;

@Getter
public class OnboardingResponseDto {
    private String message;
    private UserDto user;
    private TokenResponse newTokens;

    public OnboardingResponseDto(String message, User user, TokenResponse newTokens) {
        this.message = message;
        this.user = new UserDto(user);
        this.newTokens = newTokens;
    }

    // (보안을 위해 응답용 DTO를 따로 만듦)
    @Getter
    private static class UserDto {
        private Long id;
        private String nickname;
        private String role;

        public UserDto(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.role = user.getRole();
        }
    }
}
