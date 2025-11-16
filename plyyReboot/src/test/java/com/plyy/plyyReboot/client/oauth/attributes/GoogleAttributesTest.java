package com.plyy.plyyReboot.client.oauth.attributes;

import com.plyy.plyyReboot.client.oauth.common.AuthProvider;
import com.plyy.plyyReboot.client.oauth.common.Email;
import com.plyy.plyyReboot.client.oauth.common.ProviderId;
import com.plyy.plyyReboot.client.oauth.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("GoogleAttributes 테스트")
class GoogleAttributesTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("정상적인 attributes로 생성 성공")
        void createWithValidAttributes() {
            Map<String, Object> attributes = Map.of(
                    "sub", "google-12345",
                    "email", "test@gmail.com",
                    "name", "홍길동"
            );

            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            assertThat(googleAttributes).isNotNull();
            assertThat(googleAttributes.raw()).isEqualTo(attributes);
        }

        @Test
        @DisplayName("attributes가 null이면 MissingAttributeException 발생")
        void throwExceptionWhenAttributesIsNull() {
            assertThatThrownBy(() -> new GoogleAttributes(null))
                    .isInstanceOf(MissingAttributeException.class)
                    .hasMessageContaining("인증 정보를 받지 못했습니다");
        }

        @Test
        @DisplayName("attributes에 null 값이 있으면 MissingAttributeException 발생")
        void throwExceptionWhenAttributeValueIsNull() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", "12345");
            attributes.put("email", null);

            assertThatThrownBy(() -> new GoogleAttributes(attributes))
                    .isInstanceOf(MissingAttributeException.class)
                    .hasMessageContaining("필수 인증 정보가 누락되었습니다");
        }
    }

    @Nested
    @DisplayName("email() 테스트")
    class EmailTest {

        @Test
        @DisplayName("정상적인 이메일 추출 성공")
        void extractValidEmail() {
            Map<String, Object> attributes = Map.of(
                    "sub", "12345",
                    "email", "test@gmail.com"
            );
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            Email email = googleAttributes.email();

            assertThat(email.value()).isEqualTo("test@gmail.com");
        }

        @Test
        @DisplayName("email 필드가 없으면 MissingAttributeException 발생")
        void throwMissingExceptionWhenEmailIsMissing() {
            Map<String, Object> attributes = Map.of("sub", "12345");
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            assertThatThrownBy(() -> googleAttributes.email())
                    .isInstanceOf(MissingAttributeException.class)
                    .satisfies(ex -> {
                        MissingAttributeException exception = (MissingAttributeException) ex;
                        assertThat(exception.getProvider()).isEqualTo(AuthProvider.GOOGLE);
                        assertThat(exception.getFieldName()).isEqualTo("email");
                    });
        }

        @Test
        @DisplayName("email이 빈 문자열이면 BlankAttributeException 발생")
        void throwBlankExceptionWhenEmailIsBlank() {
            Map<String, Object> attributes = Map.of(
                    "sub", "12345",
                    "email", "   "
            );
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            assertThatThrownBy(() -> googleAttributes.email())
                    .isInstanceOf(BlankAttributeException.class)
                    .satisfies(ex -> {
                        BlankAttributeException exception = (BlankAttributeException) ex;
                        assertThat(exception.getProvider()).isEqualTo(AuthProvider.GOOGLE);
                        assertThat(exception.getFieldName()).isEqualTo("email");
                    });
        }

        @Test
        @DisplayName("email이 문자열이 아니면 InvalidAttributeTypeException 발생")
        void throwTypeExceptionWhenEmailIsNotString() {
            Map<String, Object> attributes = Map.of(
                    "sub", "12345",
                    "email", 12345
            );
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            assertThatThrownBy(() -> googleAttributes.email())
                    .isInstanceOf(InvalidAttributeTypeException.class)
                    .satisfies(ex -> {
                        InvalidAttributeTypeException exception = (InvalidAttributeTypeException) ex;
                        assertThat(exception.getProvider()).isEqualTo(AuthProvider.GOOGLE);
                        assertThat(exception.getFieldName()).isEqualTo("email");
                        assertThat(exception.getExpectedType()).isEqualTo("String");
                        assertThat(exception.getActualType()).isEqualTo("Integer");
                    });
        }

        @Test
        @DisplayName("email 형식이 잘못되면 EmailInvalidException 발생")
        void throwEmailInvalidExceptionWhenFormatIsWrong() {
            Map<String, Object> attributes = Map.of(
                    "sub", "12345",
                    "email", "invalid-email"  // @ 없음
            );
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            assertThatThrownBy(() -> googleAttributes.email())
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("유효하지 않은 이메일 형식입니다");
        }
    }

    @Nested
    @DisplayName("providerId() 테스트")
    class ProviderIdTest {

        @Test
        @DisplayName("정상적인 providerId 추출 성공")
        void extractValidProviderId() {
            Map<String, Object> attributes = Map.of(
                    "sub", "google-abc123",
                    "email", "test@gmail.com"
            );
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            ProviderId providerId = googleAttributes.providerId();

            assertThat(providerId.value()).isEqualTo("google-abc123");
        }

        @Test
        @DisplayName("sub 필드가 없으면 MissingAttributeException 발생")
        void throwExceptionWhenSubIsMissing() {
            Map<String, Object> attributes = Map.of("email", "test@gmail.com");
            GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

            assertThatThrownBy(() -> googleAttributes.providerId())
                    .isInstanceOf(MissingAttributeException.class)
                    .satisfies(ex -> {
                        MissingAttributeException exception = (MissingAttributeException) ex;
                        assertThat(exception.getFieldName()).isEqualTo("sub");
                    });
        }
    }

    @Test
    @DisplayName("provider는 항상 GOOGLE 반환")
    void returnGoogleProvider() {
        Map<String, Object> attributes = Map.of(
                "sub", "12345",
                "email", "test@gmail.com"
        );
        GoogleAttributes googleAttributes = new GoogleAttributes(attributes);

        assertThat(googleAttributes.provider()).isEqualTo(AuthProvider.GOOGLE);
    }
}