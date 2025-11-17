package com.plyy.plyyReboot.client.oauth.common;

import com.plyy.plyyReboot.client.oauth.exception.InvalidProviderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AuthProvider 테스트")
class AuthProviderTest {

    @Nested
    @DisplayName("정상 케이스")
    class ValidCases {

        @Test
        @DisplayName("kakao로 KAKAO 반환")
        void returnKakaoProvider() {
            AuthProvider provider = AuthProvider.fromRegistrationId("kakao");
            assertThat(provider).isEqualTo(AuthProvider.KAKAO);
        }

        @Test
        @DisplayName("naver로 NAVER 반환")
        void returnNaverProvider() {
            AuthProvider provider = AuthProvider.fromRegistrationId("naver");
            assertThat(provider).isEqualTo(AuthProvider.NAVER);
        }

        @Test
        @DisplayName("google로 GOOGLE 반환")
        void returnGoogleProvider() {
            AuthProvider provider = AuthProvider.fromRegistrationId("google");
            assertThat(provider).isEqualTo(AuthProvider.GOOGLE);
        }

        @Test
        @DisplayName("대소문자 구분 없이 동작")
        void caseInsensitive() {
            assertThat(AuthProvider.fromRegistrationId("KAKAO")).isEqualTo(AuthProvider.KAKAO);
            assertThat(AuthProvider.fromRegistrationId("Naver")).isEqualTo(AuthProvider.NAVER);
            assertThat(AuthProvider.fromRegistrationId("GooGle")).isEqualTo(AuthProvider.GOOGLE);
        }

        @Test
        @DisplayName("공백이 있어도 trim 처리")
        void trimSpaces() {
            assertThat(AuthProvider.fromRegistrationId("  kakao  ")).isEqualTo(AuthProvider.KAKAO);
        }
    }

    @Nested
    @DisplayName("예외 케이스")
    class ExceptionCases {

        @Test
        @DisplayName("null은 InvalidProviderException 발생")
        void throwExceptionWhenIdIsNull() {
            assertThatThrownBy(() -> AuthProvider.fromRegistrationId(null))
                    .isInstanceOf(InvalidProviderException.class)
                    .hasMessageContaining("OAuth 제공자 정보가 없습니다");
        }

        @Test
        @DisplayName("지원하지 않는 provider는 InvalidProviderException 발생")
        void throwExceptionWhenProviderIsUnsupported() {
            assertThatThrownBy(() -> AuthProvider.fromRegistrationId("facebook"))
                    .isInstanceOf(InvalidProviderException.class)
                    .hasMessageContaining("지원하지 않는 OAuth 제공자입니다")
                    .hasMessageContaining("facebook");
        }
    }
}