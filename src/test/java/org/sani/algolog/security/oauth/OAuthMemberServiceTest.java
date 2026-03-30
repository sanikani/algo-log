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
import org.springframework.dao.DataIntegrityViolationException;

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
                .nickname("octocat1")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("octocat")).thenReturn(Optional.of(savedMember));
        when(memberRepository.findByNickname("octocat1")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getEmail()).isEqualTo(userInfo.canonicalEmail());
        assertThat(result.getNickname()).isEqualTo("octocat1");
        assertThat(result.getProvider()).isEqualTo(Provider.GITHUB);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("single character login falls back to github id based nickname")
    void useSingleCharacterLoginAsNickname() {
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

    @Test
    @DisplayName("long nickname is truncated to 20 characters before uniqueness check")
    void truncateLongNicknameBeforeCheckingUniqueness() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "octocat", "abcdefghijklmnopqrstuv");
        Member savedMember = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("octocat")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("octocat")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getEmail()).isEqualTo(userInfo.canonicalEmail());
        assertThat(result.getNickname()).isEqualTo("octocat");
        assertThat(result.getProvider()).isEqualTo(Provider.GITHUB);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("long github login is truncated to 20 characters before uniqueness check")
    void truncateLongLoginBeforeCheckingUniqueness() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "abcdefghijklmnopqrstuv", "The Octocat");
        Member savedMember = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("abcdefghijklmnopqrst")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("abcdefghijklmnopqrst")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getEmail()).isEqualTo(userInfo.canonicalEmail());
        assertThat(result.getNickname()).isEqualTo("abcdefghijklmnopqrst");
        assertThat(result.getProvider()).isEqualTo(Provider.GITHUB);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("nickname suffix increments until an available candidate is found")
    void incrementNicknameSuffixAfterCollisions() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "nickname", null);
        Member occupied = Member.builder()
                .email("occupied@users.noreply.github.com")
                .nickname("nickname")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
        Member occupiedWithSuffix = Member.builder()
                .email("occupied2@users.noreply.github.com")
                .nickname("nickname1")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
        Member savedMember = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("nickname2")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail())).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("nickname")).thenReturn(Optional.of(occupied));
        when(memberRepository.findByNickname("nickname1")).thenReturn(Optional.of(occupiedWithSuffix));
        when(memberRepository.findByNickname("nickname2")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getEmail()).isEqualTo(userInfo.canonicalEmail());
        assertThat(result.getNickname()).isEqualTo("nickname2");
        assertThat(result.getProvider()).isEqualTo(Provider.GITHUB);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("nickname save collision retries with next candidate")
    void retryWhenNicknameSaveCollides() {
        GithubOAuthUserInfo userInfo = new GithubOAuthUserInfo(123L, "nickname", null);
        Member savedMember = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname("nickname1")
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        when(memberRepository.findByEmail(userInfo.canonicalEmail()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        when(memberRepository.findByNickname("nickname")).thenReturn(Optional.empty());
        when(memberRepository.findByNickname("nickname1")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate nickname"))
                .thenReturn(savedMember);

        Member result = oauthMemberService.getOrCreateGithubMember(userInfo);

        assertThat(result.getNickname()).isEqualTo("nickname1");
    }
}
