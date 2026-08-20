package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.response.AiAnalyticsResponse;
import com.propertyhub.ai.dto.response.ConversationSummaryResponse;
import com.propertyhub.ai.service.AdminAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AdminAiController {

    private final AdminAiService adminAiService;

    public AdminAiController(AdminAiService adminAiService) {
        this.adminAiService = adminAiService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryResponse>> listConversations() {
        return ResponseEntity.ok(adminAiService.listConversations());
    }

    @GetMapping("/analytics")
    public ResponseEntity<AiAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(adminAiService.getAnalytics());
    }

}
