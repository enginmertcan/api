package com.mertcanengin.api.dto.exam;

import java.util.List;

public record ExamPlayResponse(
        ExamSummaryResponse summary,
        List<ExamPlayQuestionResponse> questions
) {
}

