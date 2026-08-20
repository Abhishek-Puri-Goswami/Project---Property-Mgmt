package com.propertyhub.ai.repository;

import com.propertyhub.ai.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
