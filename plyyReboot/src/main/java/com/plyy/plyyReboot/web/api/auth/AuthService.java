package com.plyy.plyyReboot.web.api.auth;

import com.plyy.plyyReboot.config.security.RedisService;
import com.plyy.plyyReboot.config.security.jwt.JwtTokenProvider;
import com.plyy.plyyReboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * 로그아웃 처리
     * 1. Refresh Token을 Redis에서 삭제
     * 2. Access Token을 Denylist에 추가
     * @param accessToken 로그아웃할 사용자의 Access Token
     */
    @Transactional
    public void logout(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            log.warn("로그아웃 요청이 빈 토큰으로 들어왔습니다.");
            return;
        }

        if (!jwtTokenProvider.validateToken(accessToken)) {
            log.warn("로그아웃 요청이 유효하지 않은 토큰으로 들어왔습니다.");
            return;
        }

        Long userId = jwtTokenProvider.getUserId(accessToken);
        long remainingMillis = jwtTokenProvider.getRemainingMillis(accessToken);

        boolean rtDeleted = redisService.deleteRefreshToken(userId);
        if (rtDeleted) {
            log.info("로그아웃 처리: UserID {} 의 Refresh Token이 삭제되었습니다.", userId);
        } else {
            log.warn("로그아웃 처리: UserID {} 의 Refresh Token을 찾을 수 없습니다.", userId);
        }

        if (remainingMillis > 0) {
            redisService.addToDenylist(accessToken, remainingMillis);
            log.info("로그아웃 처리: UserID {} 의 Access Token이 블랙리스트에 추가되었습니다.", userId);
        }
    }

    /**
     * 회원 탈퇴 처리
     * 1. (UserService에서 DB 유저 정보 삭제)
     * 2. Refresh Token을 Redis에서 삭제
     * 3. Access Token도 Denylist에 추가
     * @param userId 탈퇴할 사용자의 ID
     */
    @Transactional
    public void withdraw(Long userId, String accessToken) {
         userRepository.deleteById(userId);
         log.info("회원 탈퇴 처리: UserID {} 가 DB에서 삭제되었습니다.", userId);


        boolean rtDeleted = redisService.deleteRefreshToken(userId);
        if (rtDeleted) {
            log.info("회원 탈퇴 처리: UserID {} 의 Refresh Token이 삭제되었습니다.", userId);
        } else {
            log.warn("회원 탈퇴 처리: UserID {} 의 Refresh Token을 찾을 수 없습니다.", userId);
        }

        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
            long remainingMillis = jwtTokenProvider.getRemainingMillis(accessToken);
            if (remainingMillis > 0) {
                redisService.addToDenylist(accessToken, remainingMillis);
                log.info("회원 탈퇴 처리: UserID {} 의 Access Token이 블랙리스트에 추가되었습니다.", userId);
            }
        } else {
            log.warn("회원 탈퇴 처리: 유효하지 않은 Access Token이 제공되어 블랙리스트 추가가 생략되었습니다. UserID: {}", userId);
        }
    }
}