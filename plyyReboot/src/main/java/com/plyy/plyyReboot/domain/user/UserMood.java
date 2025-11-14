package com.plyy.plyyReboot.domain.user;

import com.plyy.plyyReboot.domain.preference.Mood;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_mood",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_usermood_user_mood",
                        columnNames = {"user_id", "mood_id"}
                )
        }
)
public class UserMood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id", nullable = false)
    private Mood mood;

    public UserMood(User user, Mood mood) {
        this.user = user;
        this.mood = mood;
    }
}