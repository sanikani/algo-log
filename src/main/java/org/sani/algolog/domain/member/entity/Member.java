package org.sani.algolog.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.sani.algolog.global.common.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(unique = true, nullable = false, length = 20)
    @Length(min = 2, max = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(String email, String nickname, Provider provider, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.role = role;
    }
}
