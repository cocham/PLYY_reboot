package com.plyy.plyyReboot.domain.preference;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sub_genre")
public class SubGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_genre_id", nullable = false)
    private Genre parentGenre;

    @Column(name = "name", nullable = false)
    private String name;
}
