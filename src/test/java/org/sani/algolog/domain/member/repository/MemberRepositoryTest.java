package org.sani.algolog.domain.member.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.entity.Provider;
import org.sani.algolog.domain.member.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 1. JPA 관련 설정만 로드하여 가볍게 테스트 (H2 DB 자동 사용)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 성공 및 BaseEntity 작동 확인")
    void saveMember() {
        // given
        Member member = Member.builder()
                .email("test@algolog.com")
                .nickname("테스터")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@algolog.com");
         assertThat(savedMember.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findByEmail() {
        // given
        Member member = Member.builder()
                .email("email@test.com")
                .nickname("이메일찾기")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByEmail("email@test.com")
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        // then
        assertThat(findMember.getNickname()).isEqualTo("이메일찾기");
    }

    @Test
    @DisplayName("닉네임으로 회원 조회 성공")
    void findByNickname() {
        // given
        Member member = Member.builder()
                .email("nick@test.com")
                .nickname("유니크닉네임")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByNickname("유니크닉네임")
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

        // then
        assertThat(findMember.getEmail()).isEqualTo("nick@test.com");
    }
}