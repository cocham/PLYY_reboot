package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("KakaoAttributes 테스트")
class KakaoAttributesTest {

    @Nested
    @DisplayName("email() 테스트")
    class EmailTest {

        @Test
        @DisplayName("정상적인 이메일 추출 성공")
        void extractValidEmail() {
            Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of(
                            "email", "test@kakao.com",
                            "profile", Map.of("nickname", "홍길동")
                    )
            );
            KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

            Email email = kakaoAttributes.email();

            assertThat(email.value()).isEqualTo("test@kakao.com");
        }

        @Test
        @DisplayName("kakao_account 필드가 없으면 InvalidResponseStructureException 발생")
        void throwExceptionWhenKakaoAccountIsMissing() {
            Map<String, Object> attributes = Map.of("id", 12345L);
            KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

            assertThatThrownBy(() -> kakaoAttributes.email())
                    .isInstanceOf(InvalidResponseStructureException.class)
                    .satisfies(ex -> {
                        InvalidResponseStructureException exception = (InvalidResponseStructureException) ex;
                        assertThat(exception.getProvider()).isEqualTo(AuthProvider.KAKAO);
                        assertThat(exception.getMissingKey()).isEqualTo("kakao_account");
                    });
        }

        @Test
        @DisplayName("kakao_account가 Map이 아니면 InvalidResponseStructureException 발생")
        void throwExceptionWhenKakaoAccountIsNotMap() {
            Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", "not a map"
            );
            KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

            assertThatThrownBy(() -> kakaoAttributes.email())
                    .isInstanceOf(InvalidResponseStructureException.class)
                    .satisfies(ex -> {
                        InvalidResponseStructureException exception = (InvalidResponseStructureException) ex;
                        assertThat(exception.getMissingKey()).isEqualTo("kakao_account");
                        assertThat(exception.getActualType()).isEqualTo("String");
                    });
        }

        @Test
        @DisplayName("kakao_account.email이 없으면 MissingAttributeException 발생")
        void throwExceptionWhenEmailIsMissingInKakaoAccount() {
            Map<String, Object> attributes = Map.of(
                    "id", 12345L,
                    "kakao_account", Map.of("profile", Map.of("nickname", "홍길동"))
            );
            KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

            assertThatThrownBy(() -> kakaoAttributes.email())
                    .isInstanceOf(MissingAttributeException.class)
                    .satisfies(ex -> {
                        MissingAttributeException exception = (MissingAttributeException) ex;
                        assertThat(exception.getFieldName()).isEqualTo("kakao_account.email");
                    });
        }
    }

    @Nested
    @DisplayName("providerId() 테스트")
    class ProviderIdTest {

        @Test
        @DisplayName("Long 타입 ID 추출 성공")
        void extractLongId() {
            Map<String, Object> attributes = Map.of(
                    "id", 12345678L,
                    "kakao_account", Map.of("email", "test@kakao.com")
            );
            KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

            ProviderId providerId = kakaoAttributes.providerId();

            assertThat(providerId.value()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("id 필드가 없으면 MissingAttributeException 발생")
        void throwExceptionWhenIdIsMissing() {
            Map<String, Object> attributes = Map.of(
                    "kakao_account", Map.of("email", "test@kakao.com")
            );
            KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

            assertThatThrownBy(() -> kakaoAttributes.providerId())
                    .isInstanceOf(MissingAttributeException.class);
        }
    }

    @Test
    @DisplayName("provider는 항상 KAKAO 반환")
    void returnKakaoProvider() {
        Map<String, Object> attributes = Map.of(
                "id", 12345L,
                "kakao_account", Map.of("email", "test@kakao.com")
        );
        KakaoAttributes kakaoAttributes = new KakaoAttributes(attributes);

        assertThat(kakaoAttributes.provider()).isEqualTo(AuthProvider.KAKAO);
    }
}