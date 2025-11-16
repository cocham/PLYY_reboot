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

@DisplayName("NaverAttributes 테스트")
class NaverAttributesTest {

    @Nested
    @DisplayName("email() 테스트")
    class EmailTest {

        @Test
        @DisplayName("정상적인 이메일 추출 성공")
        void extractValidEmail() {
            Map<String, Object> attributes = Map.of(
                    "resultcode", "00",
                    "message", "success",
                    "response", Map.of(
                            "id", "naver-12345",
                            "email", "test@naver.com",
                            "name", "홍길동"
                    )
            );
            NaverAttributes naverAttributes = new NaverAttributes(attributes);

            Email email = naverAttributes.email();

            assertThat(email.value()).isEqualTo("test@naver.com");
        }

        @Test
        @DisplayName("response 필드가 없으면 InvalidResponseStructureException 발생")
        void throwExceptionWhenResponseIsMissing() {
            Map<String, Object> attributes = Map.of(
                    "resultcode", "00",
                    "message", "success"
            );
            NaverAttributes naverAttributes = new NaverAttributes(attributes);

            assertThatThrownBy(() -> naverAttributes.email())
                    .isInstanceOf(InvalidResponseStructureException.class)
                    .satisfies(ex -> {
                        InvalidResponseStructureException exception = (InvalidResponseStructureException) ex;
                        assertThat(exception.getProvider()).isEqualTo(AuthProvider.NAVER);
                        assertThat(exception.getMissingKey()).isEqualTo("response");
                    });
        }

        @Test
        @DisplayName("response.email이 없으면 MissingAttributeException 발생")
        void throwExceptionWhenEmailIsMissingInResponse() {
            Map<String, Object> attributes = Map.of(
                    "resultcode", "00",
                    "response", Map.of(
                            "id", "naver-12345",
                            "name", "홍길동"
                    )
            );
            NaverAttributes naverAttributes = new NaverAttributes(attributes);

            assertThatThrownBy(() -> naverAttributes.email())
                    .isInstanceOf(MissingAttributeException.class)
                    .satisfies(ex -> {
                        MissingAttributeException exception = (MissingAttributeException) ex;
                        assertThat(exception.getFieldName()).isEqualTo("response.email");
                    });
        }
    }

    @Nested
    @DisplayName("providerId() 테스트")
    class ProviderIdTest {

        @Test
        @DisplayName("정상적인 providerId 추출 성공")
        void extractLongId() {
            Map<String, Object> attributes = Map.of(
                "response", Map.of(
                        "id", "6w69bvCmUowGLB9dJ",
                        "email", "test@naver.com"
                )
            );
            NaverAttributes naverAttributes = new NaverAttributes(attributes);

            ProviderId providerId = naverAttributes.providerId();

            assertThat(providerId.value()).isEqualTo("6w69bvCmUowGLB9dJ");
        }

        @Test
        @DisplayName("id 필드가 없으면 MissingAttributeException 발생")
        void throwExceptionWhenIdIsMissing() {
            Map<String, Object> attributes = Map.of(
                "response", Map.of(
                        "email", "test@naver.com"
                )
            );
            NaverAttributes naverAttributes = new NaverAttributes(attributes);

            assertThatThrownBy(() -> naverAttributes.providerId())
                    .isInstanceOf(MissingAttributeException.class);
        }
    }

    @Test
    @DisplayName("provider는 항상 NAVER 반환")
    void returnNaverProvider() {
        Map<String, Object> attributes = Map.of(
            "response", Map.of(
                "id", "6w69bvCmUowGLB9dJ",
                "email", "test@naver.com"
            )
        );
        NaverAttributes naverAttributes = new NaverAttributes(attributes);

        assertThat(naverAttributes.provider()).isEqualTo(AuthProvider.NAVER);
    }
}
