package org.sani.algolog.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(400, "BAD_REQUEST", "요청이 올바르지 않습니다."),
    INVALID_INPUT(400, "INVALID_INPUT", "입력값 검증에 실패했습니다."),
    TYPE_MISMATCH(400, "TYPE_MISMATCH", "요청 파라미터 타입이 올바르지 않습니다."),
    MISSING_PARAMETER(400, "MISSING_PARAMETER", "필수 요청 파라미터가 누락되었습니다."),
    MESSAGE_NOT_READABLE(400, "MESSAGE_NOT_READABLE", "요청 본문을 읽을 수 없습니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다."),
    NOT_FOUND(404, "NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    CONFLICT(409, "CONFLICT", "요청이 현재 상태와 충돌합니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
