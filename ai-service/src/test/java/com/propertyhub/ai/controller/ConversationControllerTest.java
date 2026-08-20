package com.propertyhub.ai.controller;

import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.entity.MessageRole;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationRepository conversationRepository;

    @MockitoBean
    private ChatMessageRepository chatMessageRepository;

    private Conversation conversationWithId(Long id, Long userId, String title) throws Exception {
        Conversation conversation = new Conversation(userId, title);
        setField(conversation, "id", id);
        return conversation;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void returns200WithMessagesOnFound() throws Exception {
        Conversation conversation = conversationWithId(1L, 5L, "Find me a 2 BHK in Pune.");
        ChatMessage message = new ChatMessage(conversation, MessageRole.USER, "Find me a 2 BHK in Pune.");

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(chatMessageRepository.findByConversationOrderByCreatedAtAsc(conversation)).thenReturn(List.of(message));

        mockMvc.perform(get("/api/ai/conversations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.messages[0].role").value("USER"));
    }

    @Test
    void returns404WhenNotFound() throws Exception {
        when(conversationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ai/conversations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

}
