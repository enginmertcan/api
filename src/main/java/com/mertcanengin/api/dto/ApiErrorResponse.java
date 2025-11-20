package com.mertcanengin.api.dto;

import java.util.List;

public record ApiErrorResponse(
        String message,
        String errorMessage,
        String code,
        List<FieldError> details
) {

    public ApiErrorResponse {
        if (message == null || message.isBlank()) {
            message = "Beklenmeyen bir hata oluştu.";
        }
        errorMessage = (errorMessage == null || errorMessage.isBlank()) ? message : errorMessage;
        details = details == null ? List.of() : List.copyOf(details);
    }

    public static ApiErrorResponse of(String message) {
        return new ApiErrorResponse(message, message, null, List.of());
    }

    public static ApiErrorResponse validation(String message, List<FieldError> details) {
        return new ApiErrorResponse(message, message, "VALIDATION_ERROR", details);
    }

    public record FieldError(String field, String message) {
    }
}
