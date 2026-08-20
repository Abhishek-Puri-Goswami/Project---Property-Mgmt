package com.propertyhub.ai.controller;

import com.propertyhub.ai.dto.response.ChatMessageResponse;
import com.propertyhub.ai.dto.response.ConversationResponse;
import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.exception.ConversationNotFoundException;
import com.propertyhub.ai.repository.ChatMessageRepository;
import com.propertyhub.ai.repository.ConversationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/conversations")
public class ConversationController {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ConversationController(ConversationRepository conversationRepository,
                                   ChatMessageRepository chatMessageRepository) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> get(@PathVariable Long id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation with id " + id + " was not found"));

        List<ChatMessageResponse> messages = chatMessageRepository.findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .map(this::toMessageResponse)
                .toList();

        return ResponseEntity.ok(new ConversationResponse(
                conversation.getId(),
                conversation.getUserId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messages
        ));
    }

    private ChatMessageResponse toMessageResponse(ChatMessage message) {
        // Kept manual: ChatMessageResponse.role() is a String while ChatMessage.role is an
        // enum, and ModelMapper's RecordModule cannot bridge that type mismatch when
        // matching the record's canonical constructor (confirmed via a live test failure).
        return new ChatMessageResponse(
                message.getId(),
                message.getRole().name(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

}
