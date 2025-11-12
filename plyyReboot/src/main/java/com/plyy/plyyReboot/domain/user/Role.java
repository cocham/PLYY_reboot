package com.plyy.plyyReboot.domain.user;

public enum Role {
    NEW_USER("ROLE_NEW_USER"),
    USER("ROLE_USER"),
    CURATOR("ROLE_CURATOR"),
    ADMIN("ROLE_ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
