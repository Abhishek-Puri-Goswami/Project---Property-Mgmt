package com.propertyhub.ai.memory;

import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.entity.MessageRole;
import com.propertyhub.ai.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMemoryServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private Conversation conversation;

    private ChatMemoryService chatMemoryService;

    @BeforeEach
    void setUp() {
        chatMemoryService = new ChatMemoryService(chatMessageRepository);
    }

    @Test
    void returnsPlaceholderWhenNoMessages() {
        when(chatMessageRepository.findByConversationOrderByCreatedAtAsc(conversation)).thenReturn(List.of());

        String history = chatMemoryService.loadHistory(conversation);

        assertThat(history).isEqualTo("(none yet)");
    }

    @Test
    void formatsPriorMessagesInOrder() {
        ChatMessage userMsg = new ChatMessage(conversation, MessageRole.USER, "Find me a 2 BHK in Pune.");
        ChatMessage assistantMsg = new ChatMessage(conversation, MessageRole.ASSISTANT, "I found 3 matching properties.");
        when(chatMessageRepository.findByConversationOrderByCreatedAtAsc(conversation))
                .thenReturn(List.of(userMsg, assistantMsg));

        String history = chatMemoryService.loadHistory(conversation);

        assertThat(history).isEqualTo("USER: Find me a 2 BHK in Pune.\nASSISTANT: I found 3 matching properties.");
    }

}
