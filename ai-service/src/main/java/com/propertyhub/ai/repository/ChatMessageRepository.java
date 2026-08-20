package com.propertyhub.ai.repository;

import com.propertyhub.ai.entity.ChatMessage;
import com.propertyhub.ai.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);

}
