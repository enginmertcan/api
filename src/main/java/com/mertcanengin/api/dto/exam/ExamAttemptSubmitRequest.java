package com.mertcanengin.api.dto.exam;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record ExamAttemptSubmitRequest(
        @Size(min = 1, message = "En az bir cevap gönderilmelidir.")
        List<@Valid ExamAnswerRequest> answers
) {
}

