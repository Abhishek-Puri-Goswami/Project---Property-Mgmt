package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.request.ChatRequest;
import com.propertyhub.ai.dto.response.ChatResponse;
import com.propertyhub.ai.dto.response.PropertyRequirementResponse;
import com.propertyhub.ai.service.AiChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.chat(request));
    }

    @PostMapping("/requirements")
    public ResponseEntity<PropertyRequirementResponse> extractRequirement(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.extractRequirement(request));
    }

}
