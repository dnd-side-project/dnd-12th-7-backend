package com.dnd.moddo.domain.user.entity;

import com.dnd.moddo.domain.user.entity.type.Authority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String profile;

    private Boolean isMember;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public User(String name, String email, String profile, Boolean isMember, Authority authority, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.isMember = isMember;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.authority = authority;
    }
}
