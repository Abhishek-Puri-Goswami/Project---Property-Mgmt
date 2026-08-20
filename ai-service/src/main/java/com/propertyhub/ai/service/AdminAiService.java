package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.response.AiAnalyticsResponse;
import com.propertyhub.ai.dto.response.ConversationSummaryResponse;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAiService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public AdminAiService(ConversationRepository conversationRepository, ChatMessageRepository chatMessageRepository) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ConversationSummaryResponse> listConversations() {
        return conversationRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    public AiAnalyticsResponse getAnalytics() {
        long totalConversations = conversationRepository.count();
        long totalMessages = conversationRepository.findAll().stream()
                .mapToLong(chatMessageRepository::countByConversation)
                .sum();

        return new AiAnalyticsResponse(totalConversations, totalMessages);
    }

    private ConversationSummaryResponse toSummary(Conversation conversation) {
        return new ConversationSummaryResponse(
                conversation.getId(),
                conversation.getUserId(),
                conversation.getTitle(),
                chatMessageRepository.countByConversation(conversation),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

}
