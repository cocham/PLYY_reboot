package com.plyy.plyyReboot.client.oauth.common;

import com.plyy.plyyReboot.client.oauth.exception.EmailInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Email 테스트")
class EmailTest {

    @Nested
    @DisplayName("of() - 외부 입력 처리")
    class OfMethodTest {

        @Test
        @DisplayName("정상적인 이메일 생성 성공")
        void createValidEmail() {
            // given
            String rawEmail = "test@example.com";

            // when
            Email email = Email.of(rawEmail);

            // then
            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("공백이 있는 이메일은 trim 처리")
        void trimEmailWithSpaces() {
            String rawEmail = "  test@example.com  ";

            Email email = Email.of(rawEmail);

            assertThat(email.value()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("다양한 도메인 허용")
        void allowVariousDomains() {
            assertThatCode(() -> Email.of("test@gmail.com")).doesNotThrowAnyException();
            assertThatCode(() -> Email.of("user@naver.com")).doesNotThrowAnyException();
            assertThatCode(() -> Email.of("admin@company.co.kr")).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("null 이메일은 EmailInvalidException 발생")
        void throwExceptionWhenEmailIsNull() {
            assertThatThrownBy(() -> Email.of(null))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("이메일은 필수 정보입니다");
        }

        @Test
        @DisplayName("빈 문자열은 EmailInvalidException 발생")
        void throwExceptionWhenEmailIsBlank() {
            assertThatThrownBy(() -> Email.of("   "))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("이메일은 비어 있을 수 없습니다");
        }

        @Test
        @DisplayName("@ 없는 이메일은 EmailInvalidException 발생")
        void throwExceptionWhenEmailHasNoAtSign() {
            assertThatThrownBy(() -> Email.of("testexample.com"))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("유효하지 않은 이메일 형식입니다");
        }

        @Test
        @DisplayName("@ 앞이 비어있으면 EmailInvalidException 발생")
        void throwExceptionWhenLocalPartIsEmpty() {
            assertThatThrownBy(() -> Email.of("@example.com"))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("유효하지 않은 이메일 형식입니다");
        }

        @Test
        @DisplayName("@ 뒤가 비어있으면 EmailInvalidException 발생")
        void throwExceptionWhenDomainIsEmpty() {
            assertThatThrownBy(() -> Email.of("test@"))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("유효하지 않은 이메일 형식입니다");
        }

        @Test
        @DisplayName("도메인에 점(.)이 없으면 EmailInvalidException 발생")
        void throwExceptionWhenDomainHasNoDot() {
            assertThatThrownBy(() -> Email.of("test@example"))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("유효하지 않은 이메일 도메인입니다");
        }
    }

    @Nested
    @DisplayName("fromValidated() - OAuth에서 사용")
    class FromValidatedMethodTest {

        @Test
        @DisplayName("검증된 이메일로 생성 성공")
        void createFromValidatedEmail() {
            // given
            String validatedEmail = "oauth@kakao.com";

            // when
            Email email = Email.fromValidated(validatedEmail);

            // then
            assertThat(email.value()).isEqualTo("oauth@kakao.com");
        }

        @Test
        @DisplayName("형식이 잘못되면 EmailInvalidException 발생")
        void throwExceptionWhenFormatIsInvalid() {
            assertThatThrownBy(() -> Email.fromValidated("invalid-email"))
                    .isInstanceOf(EmailInvalidException.class)
                    .hasMessageContaining("유효하지 않은 이메일 형식입니다");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 이메일은 같은 객체")
        void sameEmailShouldBeEqual() {
            Email email1 = Email.of("test@example.com");
            Email email2 = Email.of("test@example.com");

            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("다른 이메일은 다른 객체")
        void differentEmailShouldNotBeEqual() {
            Email email1 = Email.of("test1@example.com");
            Email email2 = Email.of("test2@example.com");

            assertThat(email1).isNotEqualTo(email2);
        }
    }
}
