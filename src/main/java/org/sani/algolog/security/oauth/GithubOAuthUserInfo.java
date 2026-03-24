package org.sani.algolog.security.oauth;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.util.Map;

public record GithubOAuthUserInfo(
        Long githubId,
        String login,
        String name
) {

    public static GithubOAuthUserInfo from(Map<String, Object> attributes) {
        Object idValue = attributes.get("id");
        Object loginValue = attributes.get("login");
        Object nameValue = attributes.get("name");

        if (!(idValue instanceof Number idNumber)) {
            throw invalidUserInfo("GitHub user id is missing.");
        }
        if (!(loginValue instanceof String login) || login.isBlank()) {
            throw invalidUserInfo("GitHub login is missing.");
        }

        String normalizedName = nameValue instanceof String stringName ? stringName.trim() : null;
        return new GithubOAuthUserInfo(idNumber.longValue(), login.trim(), normalizedName);
    }

    public String canonicalEmail() {
        return "github-" + githubId + "@users.noreply.github.com";
    }

    public String nicknameCandidate() {
        if (name != null && !name.isBlank()) {
            return name;
        }
        return login;
    }

    private static OAuth2AuthenticationException invalidUserInfo(String message) {
        return new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"), message);
    }
}
