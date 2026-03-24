package org.sani.algolog.security.oauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.entity.Provider;
import org.sani.algolog.domain.member.entity.Role;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.global.error.exception.ConflictException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthMemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private OAuthMemberService oauthMemberService;

    @Test
    @DisplayName("existing github member is reused")
    void reuseExistingGithubMember() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "octocat", "The Octocat");
        Member member = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("octocat")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.of(member));

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result).isSameAs(member);
    }

    @Test
    @DisplayName("member with same canonical email but different provider is rejected")
    void rejectDifferentProvider() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "octocat", "The Octocat");
        Member member = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("octocat")
                .provider(Provider.KAKAO)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> oauthMemberService.getOrCreateGithubMember(userInfo))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 다른 로그인 방식으로 가입된 사용자입니다.");
    }

    @Test
    @DisplayName("new github member is created with unique nickname")
    void createGithubMember() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "octocat", "The Octocat");
        Member savedMember = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("The-Octocat1")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("The-Octocat")).thenReturn(Optional.of(savedMember));
        when(memberRepository.findByNickname("The-Octocat1")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getEmail()).isEqualTo(userInfo.canonicalEmail());
        assertThat(result.getNickname()).isEqualTo("The-Octocat1");
        assertThat(result.getProvider()).isEqualTo(Provider.GITHUB);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("short nickname falls back to github id based nickname")
    void fallbackNickname() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(7L, "x", null);
        Member savedMember = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("gh7")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("gh7")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getNickname()).isEqualTo("gh7");
    }
}
