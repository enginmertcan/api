package com.mertcanengin.api.dto.exam;

import java.util.List;

public record ExamResponse(
        ExamSummaryResponse summary,
        List<ExamQuestionResponse> questions
) {
}

