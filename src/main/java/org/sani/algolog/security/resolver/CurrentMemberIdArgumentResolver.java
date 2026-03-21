package org.sani.algolog.security.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

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
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("HttpServletRequest is required.");
        }

        String memberIdHeader = request.getHeader(MEMBER_ID_HEADER);
        if (memberIdHeader == null || memberIdHeader.isBlank()) {
            throw new ServletRequestBindingException("Missing request header '" + MEMBER_ID_HEADER + "'");
        }

        try {
            return Long.valueOf(memberIdHeader);
        } catch (NumberFormatException exception) {
            throw new MethodArgumentTypeMismatchException(
                    memberIdHeader,
                    Long.class,
                    MEMBER_ID_HEADER,
                    parameter,
                    exception
            );
        }
    }
}
