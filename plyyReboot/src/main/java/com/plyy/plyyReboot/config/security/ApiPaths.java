package com.plyy.plyyReboot.config.security;

public final class ApiPaths {
    private ApiPaths() {} // 인스턴스화 방지

    public static final String API_V1 = "/api/v1";

    // Auth
    public static final String AUTH_REFRESH = API_V1 + "/auth/refresh";

    // Users
    public static final String USERS_ONBOARDING_COMPLETE = API_V1 + "/users/onboarding/complete";
    public static final String USERS_NICKNAME_CHECK = API_V1 + "/users/nickname/check";

    // Curator
    public static final String CURATOR_BASE = API_V1 + "/curator";
}
