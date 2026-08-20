package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.exception.AiServiceException;
import com.propertyhub.ai.exception.ConversationNotFoundException;
import com.propertyhub.ai.exception.ForbiddenException;
import com.propertyhub.ai.memory.ChatMemoryService;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatMemoryService chatMemoryService;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        PromptTemplate promptTemplate = new PromptTemplate(new ClassPathResource("prompts/property-assistant.st"));
        aiChatService = new AiChatService(chatClient, promptTemplate, conversationRepository, chatMessageRepository, chatMemoryService);
    }

    private Conversation conversationWithId(Long id, Long userId) throws Exception {
        Conversation conversation = new Conversation(userId, "Some title");
        Field field = Conversation.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(conversation, id);
        return conversation;
    }

    private ChatMessage messageWithId(Long id) throws Exception {
        ChatMessage message = new ChatMessage(null, com.propertyhub.ai.entity.MessageRole.ASSISTANT, "response");
        Field field = ChatMessage.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(message, id);
        return message;
    }

    @Test
    void createsNewConversationWhenConversationIdIsNull() throws Exception {
        ChatRequest request = new ChatRequest(null, 5L, "Find me a 2 BHK in Pune under 80 lakh.");
        Conversation newConversation = conversationWithId(10L, 5L);

        when(conversationRepository.save(any(Conversation.class))).thenReturn(newConversation);
        when(chatMemoryService.loadHistory(newConversation)).thenReturn("(none yet)");
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("I found 3 matching properties.");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(messageWithId(100L));

        ChatResponse response = aiChatService.chat(request);

        assertThat(response.conversationId()).isEqualTo(10L);
        assertThat(response.messageId()).isEqualTo(100L);
        assertThat(response.response()).isEqualTo("I found 3 matching properties.");
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
    }

    @Test
    void continuesExistingConversationAndUsesHistory() throws Exception {
        Conversation existing = conversationWithId(10L, 5L);
        ChatRequest request = new ChatRequest(10L, 5L, "What budget did I mention?");

        when(conversationRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(chatMemoryService.loadHistory(existing)).thenReturn("USER: Find me a 2 BHK under 80 lakh.\nASSISTANT: Sure.");
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("You mentioned 80 lakh.");
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(messageWithId(101L));

        ChatResponse response = aiChatService.chat(request);

        assertThat(response.conversationId()).isEqualTo(10L);
        assertThat(response.response()).isEqualTo("You mentioned 80 lakh.");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestSpec).user(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("80 lakh");
    }

    @Test
    void throwsNotFoundWhenConversationDoesNotExist() {
        ChatRequest request = new ChatRequest(99L, 5L, "Hello");
        when(conversationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiChatService.chat(request))
                .isInstanceOf(ConversationNotFoundException.class);
    }

    @Test
    void throwsForbiddenWhenConversationBelongsToDifferentUser() throws Exception {
        Conversation existing = conversationWithId(10L, 999L);
        ChatRequest request = new ChatRequest(10L, 5L, "Hello");
        when(conversationRepository.findById(10L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> aiChatService.chat(request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void chatThrowsAiServiceExceptionWhenModelCallFails() throws Exception {
        Conversation newConversation = conversationWithId(11L, 5L);
        ChatRequest request = new ChatRequest(null, 5L, "Hello");

        when(conversationRepository.save(any(Conversation.class))).thenReturn(newConversation);
        when(chatMemoryService.loadHistory(newConversation)).thenReturn("(none yet)");
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("model unavailable"));

        assertThatThrownBy(() -> aiChatService.chat(request))
                .isInstanceOf(AiServiceException.class);
    }

    @Test
    void extractRequirementReturnsStructuredResponseOnSuccess() {
        ChatRequest request = new ChatRequest(null, 5L, "Find me a 2 BHK in Pune under 80 lakh with parking");
        PropertyRequirementResponse expected = new PropertyRequirementResponse("Pune", 2, new BigDecimal("8000000"), true);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(PropertyRequirementResponse.class)).thenReturn(expected);

        PropertyRequirementResponse result = aiChatService.extractRequirement(request);

        assertThat(result.city()).isEqualTo("Pune");
        assertThat(result.parkingRequired()).isTrue();
    }

    @Test
    void extractRequirementThrowsWhenParsingFails() {
        ChatRequest request = new ChatRequest(null, 5L, "Find me something");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("malformed output"));

        assertThatThrownBy(() -> aiChatService.extractRequirement(request))
                .isInstanceOf(AiServiceException.class);
    }

}
