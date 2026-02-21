package org.sani.algolog.global.error.dto;

public record FieldErrorDetail(
        String field,
        String reason,
        Object rejectedValue
) {
}
