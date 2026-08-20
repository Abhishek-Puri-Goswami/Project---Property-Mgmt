package com.propertyhub.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequest(

        Long conversationId,

        @NotNull(message = "userId is required")
        Long userId,

        @NotBlank(message = "Message is required")
        String message

) {
}
