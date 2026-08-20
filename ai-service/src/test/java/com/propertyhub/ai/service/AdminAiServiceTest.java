package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.response.AiAnalyticsResponse;
import com.propertyhub.ai.dto.response.ConversationSummaryResponse;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAiServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    private AdminAiService adminAiService;

    private Conversation conversationWithId(Long id, Long userId, String title) throws Exception {
        Conversation conversation = new Conversation(userId, title);
        Field field = Conversation.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(conversation, id);
        return conversation;
    }

    @Test
    void listConversationsReturnsSummariesWithMessageCount() throws Exception {
        adminAiService = new AdminAiService(conversationRepository, chatMessageRepository);
        Conversation conversation = conversationWithId(1L, 5L, "Find me a 2 BHK in Pune.");

        when(conversationRepository.findAll()).thenReturn(List.of(conversation));
        when(chatMessageRepository.countByConversation(conversation)).thenReturn(4L);

        List<ConversationSummaryResponse> results = adminAiService.listConversations();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).userId()).isEqualTo(5L);
        assertThat(results.get(0).messageCount()).isEqualTo(4L);
    }

    @Test
    void getAnalyticsSumsMessageCountsAcrossConversations() throws Exception {
        adminAiService = new AdminAiService(conversationRepository, chatMessageRepository);
        Conversation first = conversationWithId(1L, 5L, "Conversation one");
        Conversation second = conversationWithId(2L, 6L, "Conversation two");

        when(conversationRepository.count()).thenReturn(2L);
        when(conversationRepository.findAll()).thenReturn(List.of(first, second));
        when(chatMessageRepository.countByConversation(first)).thenReturn(3L);
        when(chatMessageRepository.countByConversation(second)).thenReturn(5L);

        AiAnalyticsResponse response = adminAiService.getAnalytics();

        assertThat(response.totalConversations()).isEqualTo(2L);
        assertThat(response.totalMessages()).isEqualTo(8L);
    }

}
