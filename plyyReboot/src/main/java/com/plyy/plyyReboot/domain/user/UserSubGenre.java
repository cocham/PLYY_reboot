package com.plyy.plyyReboot.domain.user;

import com.plyy.plyyReboot.domain.preference.SubGenre;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_sub_genre",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_usersubgenre_user_subgenre",
                        columnNames = {"user_id", "sub_genre_id"}
                )
        }
)
public class UserSubGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_genre_id", nullable = false)
    private SubGenre subGenre;

    public UserSubGenre(User user, SubGenre subGenre) {
        this.user = user;
        this.subGenre = subGenre;
    }
}