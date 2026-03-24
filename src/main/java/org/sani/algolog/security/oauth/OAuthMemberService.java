package org.sani.algolog.security.oauth;

import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.entity.Provider;
import org.sani.algolog.domain.member.entity.Role;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.global.error.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthMemberService {

    private static final int MAX_NICKNAME_LENGTH = 20;

    private final MemberRepository memberRepository;

    @Transactional
    public Member getOrCreateGithubMember(GithubOAuthUserInfo userInfo) {
        return memberRepository.findByEmail(userInfo.canonicalEmail())
                .map(this::validateGithubMember)
                .orElseGet(() -> createGithubMember(userInfo));
    }

    private Member validateGithubMember(Member member) {
        if (member.getProvider() != Provider.GITHUB) {
            throw new ConflictException("이미 다른 로그인 방식으로 가입된 사용자입니다.");
        }
        return member;
    }

    private Member createGithubMember(GithubOAuthUserInfo userInfo) {
        String nickname = generateUniqueNickname(userInfo.nicknameCandidate(), userInfo.githubId());
        Member member = Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname(nickname)
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();

        return memberRepository.save(member);
    }

    private String generateUniqueNickname(String rawNickname, Long githubId) {
        String baseNickname = normalizeNickname(rawNickname, githubId);
        String candidate = truncate(baseNickname, MAX_NICKNAME_LENGTH);
        int suffix = 1;

        while (memberRepository.findByNickname(candidate).isPresent()) {
            String suffixText = String.valueOf(suffix++);
            String truncatedBase = truncate(baseNickname, MAX_NICKNAME_LENGTH - suffixText.length());
            candidate = truncatedBase + suffixText;
        }

        return candidate;
    }

    private String normalizeNickname(String rawNickname, Long githubId) {
        String normalized = rawNickname == null ? "" : rawNickname.trim().replaceAll("\\s+", "-");
        if (normalized.length() >= 2) {
            return normalized;
        }
        return "gh" + githubId;
    }

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
