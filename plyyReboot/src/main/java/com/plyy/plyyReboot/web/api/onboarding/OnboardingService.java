package com.plyy.plyyReboot.web.api.onboarding;

import com.plyy.plyyReboot.domain.preference.*;
import com.plyy.plyyReboot.domain.user.*;
import com.plyy.plyyReboot.web.api.onboarding.dto.OnboardingRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

    private final UserRepository userRepository;

    private final GenreRepository genreRepository;
    private final SubGenreRepository subGenreRepository;
    private final MoodRepository moodRepository;
    private final UserGenreRepository userGenreRepository;
    private final UserSubGenreRepository userSubGenreRepository;
    private final UserMoodRepository userMoodRepository;

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

        if (!isNicknameAvailable(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (!user.getRole().equals("ROLE_NEW_USER")) {
            throw new IllegalStateException("이미 온보딩을 완료한 유저입니다.");
        }

        user.onboard(requestDto.getNickname(), "ROLE_USER");

        saveUserGenres(user, requestDto.getGenreIds());
        saveUserSubGenres(user, requestDto.getSubGenreIds());
        saveUserMoods(user, requestDto.getMoodIds());

        return user;
    }


    /**
     * 선호 장르 저장
     */
    private void saveUserGenres(User user, List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        List<Genre> genres = genreRepository.findAllById(genreIds);

        List<UserGenre> userGenres = genres.stream()
                .map(genre -> new UserGenre(user, genre))
                .collect(Collectors.toList());

        userGenreRepository.saveAll(userGenres);
    }

    /**
     * 선호 서브 장르 저장
     */
    private void saveUserSubGenres(User user, List<Long> subGenreIds) {
        if (subGenreIds == null || subGenreIds.isEmpty()) {
            return;
        }

        List<SubGenre> subGenres = subGenreRepository.findAllById(subGenreIds);

        List<UserSubGenre> userSubGenres = subGenres.stream()
                .map(subGenre -> new UserSubGenre(user, subGenre))
                .collect(Collectors.toList());

        userSubGenreRepository.saveAll(userSubGenres);
    }

    /**
     * 선호 무드 저장
     */
    private void saveUserMoods(User user, List<Long> moodIds) {
        if (moodIds == null || moodIds.isEmpty()) {
            return;
        }

        List<Mood> moods = moodRepository.findAllById(moodIds);

        List<UserMood> userMoods = moods.stream()
                .map(mood -> new UserMood(user, mood))
                .collect(Collectors.toList());

        userMoodRepository.saveAll(userMoods);
    }
}