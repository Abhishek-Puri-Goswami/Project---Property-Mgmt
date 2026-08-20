package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.exception.AiServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private AiChatService aiChatService;

    @Test
    void chatReturnsModelContentOnSuccess() {
        aiChatService = new AiChatService(chatClient);
        ChatRequest request = new ChatRequest(1L, "Find me a 2 BHK in Pune under 80 lakh.");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Find me a 2 BHK in Pune under 80 lakh.")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("I found 3 matching properties.");

        ChatResponse response = aiChatService.chat(request);

        assertThat(response.response()).isEqualTo("I found 3 matching properties.");
        assertThat(response.conversationId()).isEqualTo(1L);
    }

    @Test
    void chatThrowsAiServiceExceptionWhenModelCallFails() {
        aiChatService = new AiChatService(chatClient);
        ChatRequest request = new ChatRequest(null, "Hello");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user("Hello")).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("model unavailable"));

        assertThatThrownBy(() -> aiChatService.chat(request))
                .isInstanceOf(AiServiceException.class);
    }

}
