package com.propertyhub.ai.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationResponse(

        Long id,
        Long userId,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ChatMessageResponse> messages

) {
}
