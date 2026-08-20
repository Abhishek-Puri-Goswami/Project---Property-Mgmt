package com.propertyhub.ai.dto.response;

public record ChatResponse(

        Long conversationId,
        Long messageId,
        String response

) {
}
