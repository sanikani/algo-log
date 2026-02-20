package org.sani.algolog.global.error.exception;

import org.sani.algolog.global.error.ErrorCode;

public class UnauthorizedException extends AlgoLogException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
