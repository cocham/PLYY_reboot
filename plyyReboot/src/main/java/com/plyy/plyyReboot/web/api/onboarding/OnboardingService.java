package com.plyy.plyyReboot.web.api.onboarding;

import com.plyy.plyyReboot.domain.user.User;
import com.plyy.plyyReboot.domain.user.UserRepository;
import com.plyy.plyyReboot.web.api.onboarding.dto.OnboardingRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final UserRepository userRepository;

    /**
     * 닉네임 중복 확인 로직
     */
    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        return !exists;
    }

    /**
     * 온보딩 완료 로직
     */
    public User completeOnboarding(Long userId, OnboardingRequestDto requestDto) {

        // 1. 닉네임이 사용 가능한지 다시 한번 확인 (Race Condition 방지)
        if (!isNicknameAvailable(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 2. 유저를 찾아서 닉네임과 Role 업데이트
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 3. GUEST(NEW_USER)인지 확인
        if (!user.getRole().equals("ROLE_NEW_USER")) {
            throw new IllegalStateException("이미 온보딩을 완료한 유저입니다.");
        }

        // 4. 정보 업데이트
        user.onboard(requestDto.getNickname(), "ROLE_USER");

        // 5. 선호 장르/무드 저장 (향후 구현)
        // saveUserGenres(user, requestDto.getGenreIds());
        // saveUserMoods(user, requestDto.getMoodIds());

        // (JPA Dirty Checking으로 user는 자동 save됨)
        return user;
    }

}