package com.plyy.plyyReboot.client.oauth.common;

public final class Email {
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("이메일은 null일 수 없습니다.");
        }
        if (raw.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }
        if (!raw.contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다: " + raw);
        }
        return new Email(raw);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
