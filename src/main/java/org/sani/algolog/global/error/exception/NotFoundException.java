package org.sani.algolog.global.error.exception;

import org.sani.algolog.global.error.ErrorCode;

public class NotFoundException extends AlgoLogException {

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
