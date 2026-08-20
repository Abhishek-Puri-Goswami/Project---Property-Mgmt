package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.PropertySummaryDto;
import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.KnowledgeSearchResult;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.entity.MessageRole;
import com.propertyhub.ai.exception.AiServiceException;
import com.propertyhub.ai.exception.ConversationNotFoundException;
import com.propertyhub.ai.exception.ForbiddenException;
import com.propertyhub.ai.memory.ChatMemoryService;
import com.propertyhub.ai.rag.VectorSearchService;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);
    private static final int TITLE_MAX_LENGTH = 50;
    private static final String NONE = "(none)";

    private final ChatClient chatClient;
    private final PromptTemplate propertyAssistantPromptTemplate;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemoryService chatMemoryService;
    private final PropertyClientService propertyClientService;
    private final VectorSearchService vectorSearchService;

    public AiChatService(ChatClient chatClient,
                          PromptTemplate propertyAssistantPromptTemplate,
                          ConversationRepository conversationRepository,
                          ChatMessageRepository chatMessageRepository,
                          ChatMemoryService chatMemoryService,
                          PropertyClientService propertyClientService,
                          VectorSearchService vectorSearchService) {
        this.chatClient = chatClient;
        this.propertyAssistantPromptTemplate = propertyAssistantPromptTemplate;
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMemoryService = chatMemoryService;
        this.propertyClientService = propertyClientService;
        this.vectorSearchService = vectorSearchService;
    }

    @Transactional
    public ChatResponse chat(ChatRequest request) {
        log.info("AI chat request received");
        log.info("RAG processing started");

        Conversation conversation = resolveConversation(request);

        String conversationHistory = chatMemoryService.loadHistory(conversation);
        log.info("Conversation history loaded");

        String propertyContext = buildPropertyContext(request.message());
        String ragContext = buildRagContext(request.message());

        String renderedPrompt = renderPrompt(request.message(), conversationHistory, propertyContext, ragContext);

        String content;
        try {
            log.info("ChatModel request started");
            content = chatClient.prompt()
                    .user(renderedPrompt)
                    .call()
                    .content();
            log.info("ChatModel response received");
            log.info("RAG response generated");
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

        String renderedPrompt = renderPrompt(request.message(), "(none yet)", NONE, NONE);

        try {
            PropertyRequirementResponse requirement = callEntityModel(renderedPrompt);

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

    private String buildPropertyContext(String message) {
        PropertyRequirementResponse requirement;
        try {
            requirement = callEntityModel(renderPrompt(message, "(none yet)", NONE, NONE));
        } catch (Exception ex) {
            log.warn("Structured requirement extraction failed, continuing without property context", ex);
            return NONE;
        }

        boolean hasCriteria = requirement != null
                && (requirement.city() != null || requirement.bhk() != null || requirement.maxBudget() != null);

        if (!hasCriteria) {
            return NONE;
        }

        try {
            List<PropertySummaryDto> matches = propertyClientService.searchProperties(
                    requirement.city(), requirement.bhk(), null, requirement.maxBudget()
            );

            if (matches.isEmpty()) {
                return "(no matching properties found)";
            }

            return matches.stream()
                    .map(p -> "- %s in %s, %s BHK, area %s sqft, price %s, parking=%s".formatted(
                            p.title(), p.city(), p.bhk(), p.area(), p.price(), p.parking()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception ex) {
            log.warn("Property search unavailable, continuing without property context", ex);
            return "(property search unavailable)";
        }
    }

    private String buildRagContext(String message) {
        try {
            List<KnowledgeSearchResult> results = vectorSearchService.searchForContext(message);
            log.info("RAG context added");

            if (results.isEmpty()) {
                return "(no relevant knowledge found)";
            }

            return results.stream()
                    .map(r -> "[%s] %s".formatted(r.source(), r.content()))
                    .collect(Collectors.joining("\n\n"));
        } catch (Exception ex) {
            log.warn("Knowledge base search unavailable, continuing without RAG context", ex);
            return "(no relevant knowledge found)";
        }
    }

    private PropertyRequirementResponse callEntityModel(String renderedPrompt) {
        log.info("ChatModel request started");
        PropertyRequirementResponse requirement = chatClient.prompt()
                .user(renderedPrompt)
                .call()
                .entity(PropertyRequirementResponse.class);
        log.info("ChatModel response received");
        return requirement;
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

    private String renderPrompt(String userMessage, String conversationHistory, String propertyContext, String ragContext) {
        log.info("Prompt prepared");
        return propertyAssistantPromptTemplate.render(Map.of(
                "userMessage", userMessage,
                "conversationHistory", conversationHistory,
                "propertyContext", propertyContext,
                "ragContext", ragContext
        ));
    }

}
