package com.plyy.plyyReboot.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // JPA는 기본 생성자가 필수입니다.
@Entity
@Table(name = "user") // Flyway의 'user' 테이블과 매핑
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String role; // 예: "ROLE_USER", "ROLE_CURATOR"

    @Column(length = 20)
    private String provider; // 예: "kakao", "google", "naver"

    @Column(name = "social_id")
    private String socialId; // 소셜 로그인 제공사의 고유 ID

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(length = 1000)
    private String introduction; // 큐레이터 한 줄 소개

    // ===================================================
    // 큐레이터 누적 통계 (비정규화된 데이터)
    // ===================================================

    @ColumnDefault("0") // DB 기본값을 0으로 설정
    @Column(name = "playlist_count", nullable = false)
    private int playlistCount = 0;

    @ColumnDefault("0")
    @Column(name = "follower_count", nullable = false)
    private int followerCount = 0;

    @ColumnDefault("0")
    @Column(name = "total_like_count", nullable = false)
    private int totalLikeCount = 0;

    @ColumnDefault("0")
    @Column(name = "total_bookmark_count", nullable = false)
    private int totalBookmarkCount = 0;

    // ===================================================
    // 기타 메타 정보
    // ===================================================

    @Column(name = "fcm_token")
    private String fcmToken; // 푸시 알림용 토큰

    @ColumnDefault("0")
    @Column(name = "is_news_opted_in", nullable = false)
    private boolean isNewsOptedIn = false; // 마케팅 수신 동의

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt; // 마지막 로그인 일시

    @CreationTimestamp // INSERT 시 자동으로 현재 시간 저장
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 가입일

    @UpdateTimestamp // UPDATE 시 자동으로 현재 시간 저장
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 회원정보 수정일

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt; // 회원 탈퇴일 (Soft Delete 용)

    // ===================================================
    // 생성자 및 편의 메소드
    // ===================================================

    // OAuth2 신규 회원 가입을 위한 생성자
    @Builder
    public User(String email, String nickname, String role, String provider, String socialId, String thumbnailUrl) {
        this.email = email;
        this.nickname = nickname;
        this.role = role; // (CustomOAuth2UserService에서 "ROLE_USER"로 넘겨줌)
        this.provider = provider;
        this.socialId = socialId;
        this.thumbnailUrl = thumbnailUrl;
        this.lastLoginAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now(); // CreationTimestamp가 동작하지만 명시적으로 설정
        this.updatedAt = LocalDateTime.now();
    }

    // (편의 메소드) 로그인(방문) 시 마지막 로그인 일시 업데이트
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // (편의 메소드) 큐레이터 승급 로직
    public void upgradeToCurator() {
        this.role = "ROLE_CURATOR";
    }

    // (편의 메소드) 프로필 정보 업데이트 (예시)
    public void updateProfile(String nickname, String introduction, String thumbnailUrl) {
        if (nickname != null) this.nickname = nickname;
        if (introduction != null) this.introduction = introduction;
        if (thumbnailUrl != null) this.thumbnailUrl = thumbnailUrl;
    }
}