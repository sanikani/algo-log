package org.sani.algolog.security.oauth;

import lombok.RequiredArgsConstructor;
import org.sani.algolog.domain.member.entity.Member;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuthMemberService oauthMemberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        validateGithubRegistration(userRequest);

        GithubOAuthUserInfo userInfo = GithubOAuthUserInfo.from(oauth2User.getAttributes());
        Member member = oauthMemberService.getOrCreateGithubMember(userInfo);
        return new AlgoLogOAuth2User(member, oauth2User.getAttributes());
    }

    private void validateGithubRegistration(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"github".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Unsupported OAuth registration: " + registrationId);
        }
    }
}
