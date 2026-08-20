package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.entity.MessageRole;
import com.propertyhub.ai.exception.AiServiceException;
import com.propertyhub.ai.exception.ConversationNotFoundException;
import com.propertyhub.ai.exception.ForbiddenException;
import com.propertyhub.ai.memory.ChatMemoryService;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);
    private static final int TITLE_MAX_LENGTH = 50;

    private final ChatClient chatClient;
    private final PromptTemplate propertyAssistantPromptTemplate;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemoryService chatMemoryService;

    public AiChatService(ChatClient chatClient,
                          PromptTemplate propertyAssistantPromptTemplate,
                          ConversationRepository conversationRepository,
                          ChatMessageRepository chatMessageRepository,
                          ChatMemoryService chatMemoryService) {
        this.chatClient = chatClient;
        this.propertyAssistantPromptTemplate = propertyAssistantPromptTemplate;
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMemoryService = chatMemoryService;
    }

    @Transactional
    public ChatResponse chat(ChatRequest request) {
        log.info("AI chat request received");

        Conversation conversation = resolveConversation(request);

        String conversationHistory = chatMemoryService.loadHistory(conversation);
        log.info("Conversation history loaded");

        String renderedPrompt = renderPrompt(request.message(), conversationHistory);

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

        chatMessageRepository.save(new ChatMessage(conversation, MessageRole.USER, request.message()));
        ChatMessage assistantMessage = chatMessageRepository.save(
                new ChatMessage(conversation, MessageRole.ASSISTANT, content)
        );
        conversation.touch();
        log.info("AI response persisted");

        return new ChatResponse(conversation.getId(), assistantMessage.getId(), content);
    }

    public PropertyRequirementResponse extractRequirement(ChatRequest request) {
        log.info("AI chat request received");

        String renderedPrompt = renderPrompt(request.message(), "(none yet)");

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

    private Conversation resolveConversation(ChatRequest request) {
        if (request.conversationId() == null) {
            String title = deriveTitle(request.message());
            return conversationRepository.save(new Conversation(request.userId(), title));
        }

        Conversation conversation = conversationRepository.findById(request.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException(
                        "Conversation with id " + request.conversationId() + " was not found"));

        if (!conversation.getUserId().equals(request.userId())) {
            throw new ForbiddenException("Conversation does not belong to the requesting user");
        }

        return conversation;
    }

    private String deriveTitle(String message) {
        String trimmed = message.trim();
        return trimmed.length() <= TITLE_MAX_LENGTH ? trimmed : trimmed.substring(0, TITLE_MAX_LENGTH) + "...";
    }

    private String renderPrompt(String userMessage, String conversationHistory) {
        log.info("Prompt prepared");
        return propertyAssistantPromptTemplate.render(Map.of(
                "userMessage", userMessage,
                "conversationHistory", conversationHistory
        ));
    }

}
