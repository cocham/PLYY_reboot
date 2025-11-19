package com.plyy.plyyReboot.domain.preference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag", uniqueConstraints = {
        @UniqueConstraint(name = "UK_tag_name_type", columnNames = {"name", "type"})
})
@Getter
@Setter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TagType type;

    public Tag(String name, TagType type) {
        this.name = name;
        this.type = type;
    }
}
