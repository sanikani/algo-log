package org.sani.algolog.global.error.exception;

import lombok.Getter;
import org.sani.algolog.global.error.ErrorCode;

@Getter
public class AlgoLogException extends RuntimeException {

    private final ErrorCode errorCode;

    public AlgoLogException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AlgoLogException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
