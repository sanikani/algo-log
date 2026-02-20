package org.sani.algolog.global.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sani.algolog.global.error.ErrorCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("fail(ErrorCode, data) returns data with default error message")
    void failWithData() {
        List<String> details = List.of("title is required");

        ApiResponse<List<String>> response = ApiResponse.fail(ErrorCode.INVALID_INPUT, details);

        assertThat(response.getStatus()).isEqualTo(ErrorCode.INVALID_INPUT.getStatus());
        assertThat(response.getCode()).isEqualTo(ErrorCode.INVALID_INPUT.getCode());
        assertThat(response.getMessage()).isEqualTo(ErrorCode.INVALID_INPUT.getMessage());
        assertThat(response.getData()).containsExactly("title is required");
    }

    @Test
    @DisplayName("fail(ErrorCode, message, data) returns custom message and data")
    void failWithCustomMessageAndData() {
        List<String> details = List.of("field:title");
        String customMessage = "Validation failed for request.";

        ApiResponse<List<String>> response = ApiResponse.fail(ErrorCode.INVALID_INPUT, customMessage, details);

        assertThat(response.getStatus()).isEqualTo(ErrorCode.INVALID_INPUT.getStatus());
        assertThat(response.getCode()).isEqualTo(ErrorCode.INVALID_INPUT.getCode());
        assertThat(response.getMessage()).isEqualTo(customMessage);
        assertThat(response.getData()).containsExactly("field:title");
    }
}
