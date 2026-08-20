package com.propertyhub.ai.memory;

import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import com.propertyhub.ai.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMemoryService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMemoryService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public String loadHistory(Conversation conversation) {
        List<ChatMessage> messages = chatMessageRepository.findByConversationOrderByCreatedAtAsc(conversation);

        if (messages.isEmpty()) {
            return "(none yet)";
        }

        return messages.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

}
