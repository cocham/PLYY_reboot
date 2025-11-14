package com.plyy.plyyReboot.domain.user;

import com.plyy.plyyReboot.domain.preference.Genre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_genre",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_user_genre_user_genre",
                        columnNames = {"user_id", "genre_id"}
                )
        }
)

public class UserGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public UserGenre(User user, Genre genre) {
        this.user = user;
        this.genre = genre;
    }
}