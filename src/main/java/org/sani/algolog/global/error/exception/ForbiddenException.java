package org.sani.algolog.global.error.exception;

import org.sani.algolog.global.error.ErrorCode;

public class ForbiddenException extends AlgoLogException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
