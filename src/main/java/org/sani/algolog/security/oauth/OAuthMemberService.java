package org.sani.algolog.security.oauth;

import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.sani.algolog.domain.member.entity.Provider;
import org.sani.algolog.domain.member.entity.Role;
import org.sani.algolog.domain.member.repository.MemberRepository;
import org.sani.algolog.global.error.exception.ConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthMemberService {

    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final int MAX_NICKNAME_RETRY_COUNT = 5;

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
        String baseNickname = normalizeNickname(userInfo.nicknameCandidate(), userInfo.githubId());
        DataIntegrityViolationException lastException = null;

        for (int attempt = 0; attempt < MAX_NICKNAME_RETRY_COUNT; attempt++) {
            String nickname = generateNicknameCandidate(baseNickname, attempt);
            if (memberRepository.findByNickname(nickname).isPresent()) {
                continue;
            }

            try {
                return memberRepository.save(buildGithubMember(userInfo, nickname));
            } catch (DataIntegrityViolationException exception) {
                Member existingMember = memberRepository.findByEmail(userInfo.canonicalEmail())
                        .map(this::validateGithubMember)
                        .orElse(null);
                if (existingMember != null) {
                    return existingMember;
                }
                lastException = exception;
            }
        }

        throw newConflict("GitHub 닉네임을 생성하는 중 충돌이 반복되었습니다.", lastException);
    }

    private Member buildGithubMember(GithubOAuthUserInfo userInfo, String nickname) {
        return Member.builder()
                .email(userInfo.canonicalEmail())
                .nickname(nickname)
                .provider(Provider.GITHUB)
                .role(Role.USER)
                .build();
    }

    private String generateNicknameCandidate(String baseNickname, int attempt) {
        if (attempt == 0) {
            return truncate(baseNickname, MAX_NICKNAME_LENGTH);
        }

        String suffixText = String.valueOf(attempt);
        String truncatedBase = truncate(baseNickname, MAX_NICKNAME_LENGTH - suffixText.length());
        return truncatedBase + suffixText;
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

    private ConflictException newConflict(String message, Throwable cause) {
        ConflictException exception = new ConflictException(message);
        exception.initCause(cause);
        return exception;
    }
}
