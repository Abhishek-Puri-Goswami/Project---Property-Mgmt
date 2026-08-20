package com.propertyhub.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private static final String SYSTEM_PROMPT = """
            You are PropertyHub AI, an assistant for a real-estate platform.
            Give concise and useful answers. Do not invent property information.
            """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

}
