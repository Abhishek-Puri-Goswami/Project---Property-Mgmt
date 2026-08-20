package com.propertyhub.ai.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(

        Long id,
        String role,
        String content,
        LocalDateTime createdAt

) {
}
