package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.response.AiAnalyticsResponse;
import com.propertyhub.ai.dto.response.ConversationSummaryResponse;
import com.propertyhub.ai.service.AdminAiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAiController.class)
class AdminAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAiService adminAiService;

    @Test
    void returns200OnListConversations() throws Exception {
        when(adminAiService.listConversations()).thenReturn(List.of(
                new ConversationSummaryResponse(1L, 5L, "Find me a 2 BHK in Pune.", 4L, LocalDateTime.now(), LocalDateTime.now())
        ));

        mockMvc.perform(get("/api/ai/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageCount").value(4));
    }

    @Test
    void returns200OnAnalytics() throws Exception {
        when(adminAiService.getAnalytics()).thenReturn(new AiAnalyticsResponse(12L, 48L));

        mockMvc.perform(get("/api/ai/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalConversations").value(12))
                .andExpect(jsonPath("$.totalMessages").value(48));
    }

}
