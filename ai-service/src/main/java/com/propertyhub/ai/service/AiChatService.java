package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final ChatClient chatClient;

    public AiChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ChatResponse chat(ChatRequest request) {
        log.info("AI chat request received");

        String content;
        try {
            log.info("Prompt prepared");
            log.info("ChatModel request started");
            content = chatClient.prompt()
                    .user(request.message())
                    .call()
                    .content();
            log.info("ChatModel response received");
        } catch (Exception ex) {
            log.error("AI model invocation failed", ex);
            throw new AiServiceException("Failed to get a response from the AI model", ex);
        }

        return new ChatResponse(request.conversationId(), null, content);
    }

}
