package com.plyy.plyyReboot.web.api.controller;

import com.plyy.plyyReboot.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional; // (DB 테스트를 위해 추가)

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // (1) 모든 Bean(SecurityConfig, RedisConfig 등)을 로드
@AutoConfigureMockMvc // (2) MockMvc 주입
@Transactional // (DB를 사용하는 테스트의 경우 롤백 - 지금은 필요 없지만 좋은 습관)
class LoginControllerTest { // (클래스 이름은 PascalCase로 수정)

    @Autowired
    private MockMvc mvc; // (3) API 요청용 가짜 브라우저

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // (4) 테스트용 "실제 토큰" 생성기

    @Test
    @DisplayName("인증 성공: 유효한 토큰으로 /me 요청 시 200 OK와 User ID 반환")
    void getMyInfo_Success() throws Exception {
        // given
        // (5) "유저 ID가 1이고 역할이 ROLE_USER"인 실제 토큰을 생성
        // (createTokens가 Access/Refresh 둘 다 만드니 accessToken만 꺼내 씀)
        String accessToken = jwtTokenProvider.createTokens(1L, "ROLE_USER").accessToken();

        // when & then
        mvc.perform(get("/api/v1/test/me")
                        .header("Authorization", "Bearer " + accessToken)) // (6) "실제" 헤더에 토큰 삽입
                .andDo(print()) // 로그 출력
                .andExpect(status().isOk()) // (7) 200 OK 응답 검증
                .andExpect(content().string("테스트 성공! 당신의 User ID는 1 입니다.")); // (8) 본문 내용 검증
    }

    @Test
    @DisplayName("인증 실패: 토큰 없이 /me 요청 시 401 Unauthorized 반환")
    void getMyInfo_Fail_NoToken() throws Exception {
        // given
        // (토큰 없음)

        // when & then
        mvc.perform(get("/api/v1/test/me")) // (9) 헤더 없이 요청
                .andDo(print()) // 로그 출력
                .andExpect(status().isUnauthorized()); // (10) 401 응답 검증 (SecurityConfig 설정)
    }

    @Test
    @DisplayName("인증 실패: 잘못된 토큰으로 /me 요청 시 401 Unauthorized 반환")
    void getMyInfo_Fail_InvalidToken() throws Exception {
        // given
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.token"; // (아무 가짜 토큰)

        // when & then
        mvc.perform(get("/api/v1/test/me")
                        .header("Authorization", "Bearer " + invalidToken)) // (11) 가짜 토큰으로 요청
                .andDo(print()) // 로그 출력
                .andExpect(status().isUnauthorized()); // (12) 401 응답 검증 (JwtTokenProvider가 실패)
    }
}