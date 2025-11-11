package com.plyy.plyyReboot.web.api.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.List;

@Getter
public class OnboardingRequestDto {
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    @Size(min = 1, message = "선호 장르를 1개 이상 선택해주세요.")
    private List<Long> genreIds;
    private List<Long> subGenreIds;

    @Size(min = 1, message = "선호 무드를 1개 이상 선택해주세요.")
    private List<Long> moodIds;
}
