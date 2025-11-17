package com.plyy.plyyReboot.client.oauth.common;

import com.plyy.plyyReboot.client.oauth.exception.ProviderIdNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ProviderId 테스트")
class ProviderIdTest {

    @Nested
    @DisplayName("of() - 외부 입력 처리")
    class OfMethodTest {

        @Test
        @DisplayName("정상적인 ProviderId 생성 성공")
        void createValidProviderId() {
            ProviderId providerId = ProviderId.of("kakao-12345");
            assertThat(providerId.value()).isEqualTo("kakao-12345");
        }

        @Test
        @DisplayName("공백이 있는 ID는 trim 처리")
        void trimIdWithSpaces() {
            ProviderId providerId = ProviderId.of("  google-abc123  ");
            assertThat(providerId.value()).isEqualTo("google-abc123");
        }

        @Test
        @DisplayName("숫자만 있는 ID도 허용")
        void allowNumericId() {
            assertThatCode(() -> ProviderId.of("12345678"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null ID는 ProviderIdNotFoundException 발생")
        void throwExceptionWhenIdIsNull() {
            assertThatThrownBy(() -> ProviderId.of(null))
                    .isInstanceOf(ProviderIdNotFoundException.class)
                    .hasMessageContaining("Provider ID는 필수 정보입니다");
        }

        @Test
        @DisplayName("빈 문자열은 ProviderIdNotFoundException 발생")
        void throwExceptionWhenIdIsBlank() {
            assertThatThrownBy(() -> ProviderId.of("   "))
                    .isInstanceOf(ProviderIdNotFoundException.class)
                    .hasMessageContaining("Provider ID는 비어 있을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("fromValidated() - OAuth에서 사용")
    class FromValidatedMethodTest {

        @Test
        @DisplayName("검증된 ID로 생성 성공")
        void createFromValidatedId() {
            ProviderId providerId = ProviderId.fromValidated("naver-xyz789");
            assertThat(providerId.value()).isEqualTo("naver-xyz789");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 ID는 같은 객체")
        void sameIdShouldBeEqual() {
            ProviderId id1 = ProviderId.of("test-123");
            ProviderId id2 = ProviderId.of("test-123");

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }
    }
}