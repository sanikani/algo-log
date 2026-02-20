package org.sani.algolog.global.error;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.sani.algolog.global.common.ApiResponse;
import org.sani.algolog.global.error.dto.FieldErrorDetail;
import org.sani.algolog.global.error.exception.AlgoLogException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlgoLogException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlgoLogException(AlgoLogException exception) {
        return errorResponse(exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDetail>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        List<FieldErrorDetail> fieldErrors = extractFieldErrors(exception.getBindingResult());
        return errorResponse(ErrorCode.INVALID_INPUT, ErrorCode.INVALID_INPUT.getMessage(), fieldErrors);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDetail>>> handleBindException(BindException exception) {
        List<FieldErrorDetail> fieldErrors = extractFieldErrors(exception.getBindingResult());
        return errorResponse(ErrorCode.INVALID_INPUT, ErrorCode.INVALID_INPUT.getMessage(), fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDetail>>> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        List<FieldErrorDetail> fieldErrors = exception.getConstraintViolations().stream()
                .map(violation -> new FieldErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .toList();

        return errorResponse(ErrorCode.INVALID_INPUT, ErrorCode.INVALID_INPUT.getMessage(), fieldErrors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ) {
        String requiredType = exception.getRequiredType() == null
                ? "unknown"
                : exception.getRequiredType().getSimpleName();

        String message = "파라미터 '" + exception.getName() + "'의 타입은 " + requiredType + " 이어야 합니다.";
        return errorResponse(ErrorCode.TYPE_MISMATCH, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception
    ) {
        String message = "필수 파라미터 '" + exception.getParameterName() + "'가 누락되었습니다.";
        return errorResponse(ErrorCode.MISSING_PARAMETER, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception
    ) {
        String message = "'" + exception.getMethod() + "' 메서드는 이 엔드포인트에서 지원하지 않습니다.";
        return errorResponse(ErrorCode.METHOD_NOT_ALLOWED, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return errorResponse(ErrorCode.MESSAGE_NOT_READABLE, ErrorCode.MESSAGE_NOT_READABLE.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Unhandled exception occurred", exception);
        return errorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    private List<FieldErrorDetail> extractFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(this::toFieldErrorDetail)
                .toList();
    }

    private FieldErrorDetail toFieldErrorDetail(org.springframework.validation.FieldError fieldError) {
        String reason = fieldError.getDefaultMessage() == null || fieldError.getDefaultMessage().isBlank()
                ? "잘못된 값입니다."
                : fieldError.getDefaultMessage();

        return new FieldErrorDetail(fieldError.getField(), reason, fieldError.getRejectedValue());
    }

    private ResponseEntity<ApiResponse<Void>> errorResponse(ErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, message));
    }

    private <T> ResponseEntity<ApiResponse<T>> errorResponse(ErrorCode errorCode, String message, T data) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, message, data));
    }
}
