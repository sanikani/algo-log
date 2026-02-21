package org.sani.algolog.global.error.exception;

import org.sani.algolog.global.error.ErrorCode;

public class BadRequestException extends AlgoLogException {

    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
