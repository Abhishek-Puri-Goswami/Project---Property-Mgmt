package com.propertyhub.ai.dto.response;

import java.time.LocalDateTime;

public record ConversationSummaryResponse(

        Long id,
        Long userId,
        String title,
        long messageCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
