package org.sani.algolog.global.error.exception;

import org.sani.algolog.global.error.ErrorCode;

public class ConflictException extends AlgoLogException {

    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
