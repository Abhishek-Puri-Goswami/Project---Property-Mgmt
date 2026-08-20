package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.service.AiChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiChatController.class)
class AiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiChatService aiChatService;

    @Test
    void returns200OnValidMessage() throws Exception {
        when(aiChatService.chat(any())).thenReturn(new ChatResponse(1L, null, "I found 3 matching properties."));

        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"conversationId":1,"message":"Find me a 2 BHK in Pune under 80 lakh."}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("I found 3 matching properties."));
    }

    @Test
    void returns400OnBlankMessage() throws Exception {
        mockMvc.perform(post("/api/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void returns200OnValidRequirementExtraction() throws Exception {
        when(aiChatService.extractRequirement(any())).thenReturn(
                new PropertyRequirementResponse("Pune", 2, new BigDecimal("8000000"), true)
        );

        mockMvc.perform(post("/api/ai/requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"Find me a 2 BHK in Pune under 80 lakh with parking"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Pune"))
                .andExpect(jsonPath("$.bhk").value(2))
                .andExpect(jsonPath("$.parkingRequired").value(true));
    }

}
