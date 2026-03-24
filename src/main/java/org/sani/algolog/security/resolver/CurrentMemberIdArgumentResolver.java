package org.sani.algolog.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.sani.algolog.global.error.exception.UnauthorizedException;
import org.sani.algolog.security.oauth.AlgoLogAuthenticatedPrincipal;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMemberId.class)
                && (Long.class.equals(parameter.getParameterType()) || long.class.equals(parameter.getParameterType()));
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            org.springframework.web.bind.support.WebDataBinderFactory binderFactory
    ) throws Exception {
        Authentication authentication = resolveAuthentication(webRequest);
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("인증된 사용자 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AlgoLogAuthenticatedPrincipal authenticatedPrincipal)) {
            throw new UnauthorizedException("현재 사용자 정보를 확인할 수 없습니다.");
        }

        return authenticatedPrincipal.getMemberId();
    }

    private Authentication resolveAuthentication(NativeWebRequest webRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication;
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }

        Object requestContext = request.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (requestContext instanceof SecurityContext securityContext && securityContext.getAuthentication() != null) {
            return securityContext.getAuthentication();
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object sessionContext = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (sessionContext instanceof SecurityContext securityContext) {
            return securityContext.getAuthentication();
        }

        return null;
    }
}
