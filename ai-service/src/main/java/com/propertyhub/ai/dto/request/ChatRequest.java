package com.propertyhub.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(

        Long conversationId,

        @NotBlank(message = "Message is required")
        String message

) {
}
