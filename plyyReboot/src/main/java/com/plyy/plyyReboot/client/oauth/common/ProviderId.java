package com.plyy.plyyReboot.client.oauth.common;

public final class ProviderId {
    private final String value;
    private ProviderId(String value) {
        this.value = value;
    }

    public static ProviderId of(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Provider ID는 null일 수 없습니다.");
        }
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Provider ID는 비어 있을 수 없습니다.");
        }
        return new ProviderId(raw);
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
        if (!(obj instanceof ProviderId other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
