package com.plyy.plyyReboot.domain.user;

import com.plyy.plyyReboot.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
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
        this.role = role;
        this.provider = provider;
        this.socialId = socialId;
        this.thumbnailUrl = thumbnailUrl;
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void upgradeToCurator() {
        this.role = "ROLE_CURATOR";
    }

    public void updateProfile(String nickname, String introduction, String thumbnailUrl) {
        if (nickname != null) this.nickname = nickname;
        if (introduction != null) this.introduction = introduction;
        if (thumbnailUrl != null) this.thumbnailUrl = thumbnailUrl;
    }

    public void onboard(String nickname, String role) {
        this.nickname = nickname;
        this.role = role;
    }
}