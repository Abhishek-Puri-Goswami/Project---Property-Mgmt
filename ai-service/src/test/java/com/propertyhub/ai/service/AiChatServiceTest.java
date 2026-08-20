package com.propertyhub.ai.service;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.exception.AiServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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

    @BeforeEach
    void setUp() {
        PromptTemplate promptTemplate = new PromptTemplate(new ClassPathResource("prompts/property-assistant.st"));
        aiChatService = new AiChatService(chatClient, promptTemplate);
    }

    @Test
    void chatReturnsModelContentOnSuccess() {
        ChatRequest request = new ChatRequest(1L, "Find me a 2 BHK in Pune under 80 lakh.");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn("I found 3 matching properties.");

        ChatResponse response = aiChatService.chat(request);

        assertThat(response.response()).isEqualTo("I found 3 matching properties.");
        assertThat(response.conversationId()).isEqualTo(1L);
    }

    @Test
    void chatThrowsAiServiceExceptionWhenModelCallFails() {
        ChatRequest request = new ChatRequest(null, "Hello");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("model unavailable"));

        assertThatThrownBy(() -> aiChatService.chat(request))
                .isInstanceOf(AiServiceException.class);
    }

    @Test
    void extractRequirementReturnsStructuredResponseOnSuccess() {
        ChatRequest request = new ChatRequest(null, "Find me a 2 BHK in Pune under 80 lakh with parking");
        PropertyRequirementResponse expected = new PropertyRequirementResponse("Pune", 2, new BigDecimal("8000000"), true);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(PropertyRequirementResponse.class)).thenReturn(expected);

        PropertyRequirementResponse result = aiChatService.extractRequirement(request);

        assertThat(result.city()).isEqualTo("Pune");
        assertThat(result.bhk()).isEqualTo(2);
        assertThat(result.parkingRequired()).isTrue();
    }

    @Test
    void extractRequirementThrowsWhenModelReturnsEmptyResponse() {
        ChatRequest request = new ChatRequest(null, "Find me something");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(PropertyRequirementResponse.class)).thenReturn(null);

        assertThatThrownBy(() -> aiChatService.extractRequirement(request))
                .isInstanceOf(AiServiceException.class);
    }

    @Test
    void extractRequirementThrowsWhenParsingFails() {
        ChatRequest request = new ChatRequest(null, "Find me something");

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenThrow(new RuntimeException("malformed output"));

        assertThatThrownBy(() -> aiChatService.extractRequirement(request))
                .isInstanceOf(AiServiceException.class);
    }

}
