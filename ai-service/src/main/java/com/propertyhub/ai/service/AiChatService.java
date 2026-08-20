package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.exception.AiServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final ChatClient chatClient;
    private final PromptTemplate propertyAssistantPromptTemplate;

    public AiChatService(ChatClient chatClient, PromptTemplate propertyAssistantPromptTemplate) {
        this.chatClient = chatClient;
        this.propertyAssistantPromptTemplate = propertyAssistantPromptTemplate;
    }

    public ChatResponse chat(ChatRequest request) {
        log.info("AI chat request received");

        String renderedPrompt = renderPrompt(request.message());

        String content;
        try {
            log.info("ChatModel request started");
            content = chatClient.prompt()
                    .user(renderedPrompt)
                    .call()
                    .content();
            log.info("ChatModel response received");
        } catch (Exception ex) {
            log.error("AI model invocation failed", ex);
            throw new AiServiceException("Failed to get a response from the AI model", ex);
        }

        return new ChatResponse(request.conversationId(), null, content);
    }

    public PropertyRequirementResponse extractRequirement(ChatRequest request) {
        log.info("AI chat request received");

        String renderedPrompt = renderPrompt(request.message());

        try {
            log.info("ChatModel request started");
            PropertyRequirementResponse requirement = chatClient.prompt()
                    .user(renderedPrompt)
                    .call()
                    .entity(PropertyRequirementResponse.class);
            log.info("ChatModel response received");

            if (requirement == null) {
                throw new AiServiceException("AI model returned an empty structured response");
            }

            return requirement;
        } catch (AiServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("AI model invocation failed", ex);
            throw new AiServiceException("Failed to parse structured property requirement from AI response", ex);
        }
    }

    private String renderPrompt(String userMessage) {
        log.info("Prompt prepared");
        return propertyAssistantPromptTemplate.render(Map.of(
                "userMessage", userMessage,
                "conversationHistory", "(none yet)"
        ));
    }

}
